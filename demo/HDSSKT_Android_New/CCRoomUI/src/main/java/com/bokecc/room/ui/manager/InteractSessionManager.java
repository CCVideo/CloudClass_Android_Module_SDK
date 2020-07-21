package com.bokecc.room.ui.manager;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.model.ChatEntity;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.sskt.base.bean.BallotResult;
import com.bokecc.sskt.base.bean.BrainStom;
import com.bokecc.sskt.base.bean.CCPublicStream;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.ChatMsg;
import com.bokecc.sskt.base.bean.SendReward;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.bean.Vote;
import com.bokecc.sskt.base.bean.VoteResult;
import com.bokecc.sskt.base.callback.OnAnswerNamedListener;
import com.bokecc.sskt.base.callback.OnAtlasServerListener;
import com.bokecc.sskt.base.callback.OnBallotListener;
import com.bokecc.sskt.base.callback.OnBrainStomListener;
import com.bokecc.sskt.base.callback.OnClassStatusListener;
import com.bokecc.sskt.base.callback.OnDoubleTeacherIsDrawListener;
import com.bokecc.sskt.base.callback.OnDoubleTeacherListener;
import com.bokecc.sskt.base.callback.OnFollowUpdateListener;
import com.bokecc.sskt.base.callback.OnInterWramMediaListener;
import com.bokecc.sskt.base.callback.OnInterludeMediaListener;
import com.bokecc.sskt.base.callback.OnLockListener;
import com.bokecc.sskt.base.callback.OnMediaListener;
import com.bokecc.sskt.base.callback.OnMediaModeUpdateListener;
import com.bokecc.sskt.base.callback.OnMediaSyncListener;
import com.bokecc.sskt.base.callback.OnNotifyStreamListener;
import com.bokecc.sskt.base.callback.OnPublishStreamErrListener;
import com.bokecc.sskt.base.callback.OnReceiveNamedListener;
import com.bokecc.sskt.base.callback.OnRecivePublishError;
import com.bokecc.sskt.base.callback.OnRollCallListener;
import com.bokecc.sskt.base.callback.OnRoomTimerListener;
import com.bokecc.sskt.base.callback.OnSendCupListener;
import com.bokecc.sskt.base.callback.OnSendFlowerListener;
import com.bokecc.sskt.base.callback.OnSendHammerListener;
import com.bokecc.sskt.base.callback.OnServerListener;
import com.bokecc.sskt.base.callback.OnStartNamedListener;
import com.bokecc.sskt.base.callback.OnStreamStatsListener;
import com.bokecc.sskt.base.callback.OnSwitchSpeak;
import com.bokecc.sskt.base.callback.OnTalkerAudioStatusListener;
import com.bokecc.sskt.base.callback.OnTeacherDownListener;
import com.bokecc.sskt.base.callback.OnTeacherGoListener;
import com.bokecc.sskt.base.callback.OnTemplateTypeUpdateListener;
import com.bokecc.sskt.base.callback.OnUserCountUpdateListener;
import com.bokecc.sskt.base.callback.OnVideoControlListener;
import com.bokecc.ccdocview.model.DocInfo;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.model.MyEBEvent;
import com.example.ccbarleylibrary.CCBarLeyManager;
import com.example.ccchatlibrary.CCChatManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 作者 ${CC视频}.<br/>
 */

public class InteractSessionManager {

    private EventBus mEventBus;
    private CCAtlasClient mInteractSession;
    private CCChatManager ccChatManager;
    private CCBarLeyManager ccBarLeyManager;
    private CCDocViewManager ccDocViewManager;
    private static InteractSessionManager instance;

    public static InteractSessionManager getInstance() {
        if (instance == null) {
            synchronized (InteractSessionManager.class) {
                if (instance == null) {
                    instance = new InteractSessionManager();
                }
            }
        }
        return instance;
    }

    private InteractSessionManager() {
        mInteractSession = CCAtlasClient.getInstance();
        ccChatManager = CCChatManager.getInstance();
        ccBarLeyManager = CCBarLeyManager.getInstance();
        ccDocViewManager = CCDocViewManager.getInstance();
//        addInteractListeners();
    }

    public void setEventBus(EventBus eventBus) {
        mEventBus = eventBus;
    }

    /**
     * 聊天监听事件
     */
    private CCChatManager.OnChatListener mChatListener = new CCChatManager.OnChatListener() {
        @Override
        public void onReceived(CCUser from, ChatMsg msg, boolean self) {
            final ChatEntity chatEntity = new ChatEntity();
            chatEntity.setType(msg.getType());
            chatEntity.setUserId(from.getUserId());
            chatEntity.setUserName(from.getUserName());
            chatEntity.setMsg(msg.getMsg());
            chatEntity.setTime(msg.getTime());
            chatEntity.setSelf(self);
            chatEntity.setUserRole(from.getUserRole());
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CHAT, chatEntity, self));
        }

        @Override
        public void onError(String err) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ERROR, err));
        }
    };
    /**
     * 获取人数 包含总人数和旁听人数 的监听事件
     */
    private OnUserCountUpdateListener mUserCountUpdateListener = new OnUserCountUpdateListener() {
        @Override
        public void onUpdate(int classCount, int audienceCount) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_COUNT, classCount, audienceCount));
        }
    };
    //直播间设置监听事件
    private CCBarLeyManager.OnUserListUpdateListener mUserListUpdateListener = new CCBarLeyManager.OnUserListUpdateListener() {
        @Override
        public void onUpdateUserList(final ArrayList<CCUser> users) {
            if(mEventBus != null){
                mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_LIST, users));
            }
        }
    };
    /**
     * 被禁言监听事件
     */
    private CCChatManager.OnGagListener mGagUpdateListener = new CCChatManager.OnGagListener() {
        @Override
        public void onChatGagOne(String userid, boolean isAllowChat) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_GAG, userid, isAllowChat));
        }

        @Override
        public void onChatGagAll(boolean isAllowChat) {
            //老师调用全体禁言，学生接收监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ALL_GAG, isAllowChat));
        }
    };
    /**
     * 设为讲师监听事件
     */
    private CCDocViewManager.OnTalkerAuthDocListener mAuthDrawListener = new CCDocViewManager.OnTalkerAuthDocListener() {

        @Override
        public void onAuth(String userid, boolean isAllowDraw) {
            //学生授权标注监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_AUTH_DRAW, userid, isAllowDraw));
        }

        @Override
        public void onSetTeacherStatus(String userid, boolean isAllowDraw) {//设为讲师
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SETUP_THEACHER, userid, isAllowDraw));
        }

        @Override
        public void onSetTeacherToPage(DocInfo mDocInfo, int position) {//设为讲师以后翻页事件的监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SETUP_THEACHER_PAGE, mDocInfo, position));
        }

        @Override
        public void onSetTeacherToDoc(DocInfo mDocInfo, int position) {//设为讲师以后文档加载监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOC_CHANGE, mDocInfo, position));
        }

        @Override
        public void onTeacherToTalkerAuth(int position) {//老师设学生为讲师，学生翻页，老师也需要监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEACHER_SETUPTHEACHER_FLAG, position));
        }
    };


    /**
     *
     */
    private OnMediaListener mAudioListener = new OnMediaListener() {
        @Override
        public void onAudio(String userid, boolean isAllowAudio, boolean isSelf) {//学生麦克风摄像头状态被动变化监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_AUDIO, userid, isAllowAudio, isSelf));
        }

        @Override
        public void onVideo(String userid, boolean isAllowVideo, boolean isSelf) {//学生摄像头状态被动变化监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_USER_VIDEO, userid, isAllowVideo, isSelf));
        }
    };

    /**
     * 学生被老师提出房间监听事件
     */
    private CCBarLeyManager.OnKickOutListener mKickOutListener = new CCBarLeyManager.OnKickOutListener() {
        @Override
        public void onKickOut() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_KICK_OUT));
        }

        @Override
        public void onForceOut() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_FORCE_OUT));
        }
    };
    /**
     * 学生排麦麦序更新监听事件
     */
    private CCBarLeyManager.OnQueueMaiUpdateListener mQueueMaiUpdateListener = new CCBarLeyManager.OnQueueMaiUpdateListener() {
        @Override
        public void onUpdateBarLeyStatus(ArrayList<CCUser> users) {//排麦的回调
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_QUEUE_MAI, users));
        }
    };
    /**
     * 学生排麦状态监听事件
     */
    private CCBarLeyManager.OnNotifyMaiStatusLisnter mNotifyMaiStatusLisnter = new CCBarLeyManager.OnNotifyMaiStatusLisnter() {
        @Override
        public void onUpMai(int oldStatus) {//上麦事件监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UP_MAI, oldStatus));
        }

        @Override
        public void onDownMai() {//下麦事件监听
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOWN_MAI));
        }
    };
    /**
     * 连麦多媒体模式变化回调
     * <p>
     */
    private OnMediaModeUpdateListener mMediaModeUpdateListener = new OnMediaModeUpdateListener() {
        @Override
        public void onUpdate(@CCAtlasClient.MediaMode int mode) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE, mode));
        }
    };
    /**
     * 连麦模式更新回调
     * <p>
     */
    private CCBarLeyManager.OnSpeakModeUpdateListener mLianmaiModeUpdateListener = new CCBarLeyManager.OnSpeakModeUpdateListener() {
        @Override
        public void onBarLeyMode(int mode) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE, mode));
        }
    };
    /**
     * 流变化通知
     */
    private OnNotifyStreamListener mNotifyStreamListener = new OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(SubscribeRemoteStream remoteStream) {
            mEventBus.postSticky(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ADDED, remoteStream));
        }

        @Override
        public void onStreamRemoved(SubscribeRemoteStream remoteStream) {//移除流回调
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_REMOVED, remoteStream));
        }

        @Override
        public void onServerInitSuccess() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_INIT, true));
        }

        @Override
        public void onServerInitFail() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_INIT, false));
        }

        @Override
        public void onServerConnected() {

        }

        @Override
        public void onServerReconnect() {

        }

        @Override
        public void onStreamError() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ERROR));
        }

        @Override
        public void onServerDisconnected() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ATLAS_SERVER_DISCONNECTED));
        }

        @Override
        public void onStartRouteOptimization() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_START_OPT));
        }

        @Override
        public void onStopRouteOptimization() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_STOP_OPT));
        }

        @Override
        public void onRouteOptimizationError(String msg) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_SWITCH_ERROR, msg));
        }

        @Override
        public void onReloadPreview() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_RELOAD_PREVIEW));
        }

        @Override
        public void onStudentDownMai() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_SWITCH_STUDENT_DOWNMAI));
        }

    };
    /**
     * 收到教师点名回调
     */
    private OnReceiveNamedListener mStartRollCallListener = new OnReceiveNamedListener() {
        @Override
        public void onReceived(int namedTime) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_START_NAMED, namedTime));
        }
    };
    /**
     * 教师开始点名回调
     */
    private OnStartNamedListener mRollCallListListener = new OnStartNamedListener() {
        @Override
        public void onStartNamedResult(boolean isAllow, ArrayList<String> ids) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROLL_CALL_LIST, isAllow, ids));
        }
    };
    /**
     * 学生应答点名回调
     */
    private OnAnswerNamedListener mAnswerRollCallListener = new OnAnswerNamedListener() {
        @Override
        public void onAnswered(String answerUserId, ArrayList<String> answerIds) {
            Tools.log("listener","发送。。INTERACT_EVENT_WHAT_ANSWER_NAMED");
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ANSWER_NAMED, answerUserId, answerIds));
        }
    };
    /**
     * 监听直播间的状态事件
     */
    private OnServerListener mServerDisconnectListener = new OnServerListener() {
        @Override
        public void onDisconnect(int platform) {//直播间断开
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT, platform));
        }

        @Override
        public void onConnect() {//直播间连接上
//            CCApplication.isConnect = true;//todo
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_CONNECT));
        }

        @Override
        public void onReconnect() {

        }

        @Override
        public void onReconnecting() {

        }
    };
    /**
     * 学生收到老师上麦邀请回调
     * <p>
     */
    private CCBarLeyManager.OnNotifyInviteListener mNotifyInviteListener = new CCBarLeyManager.OnNotifyInviteListener() {
        @Override
        public void onInvite() {//被邀请监听事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INVITE));
        }

        @Override
        public void onCancel() {//取消邀请连接事件
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INVITE_CANCEL));
        }
    };
    /**
     * 上课状态变化通知
     * <p>
     */
    private OnClassStatusListener mClassStatusListener = new OnClassStatusListener() {
        @Override
        public void onStart() {//开始上课事件通知
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START));
        }

        @Override
        public void onStop() {//下课事件通知
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP));
        }
    };
    /**
     * 主视频模式下，主视频更新回调
     * <p>
     */
    private OnFollowUpdateListener mFollowUpdateListener = new OnFollowUpdateListener() {
        @Override
        public void onFollow(String userid) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_MAIN_VIDEO_FOLLOW, userid));
        }
    };
    /**
     * 模板更新回调
     * <p>
     */
    private OnTemplateTypeUpdateListener mTemplateTypeUpdateListener = new OnTemplateTypeUpdateListener() {
        @Override
        public void onTemplateUpdate(@CCAtlasClient.Template int template) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEMPLATE, template));
        }
    };
    /**
     * 教师异常下线回调
     * <p>
     */
    private OnTeacherDownListener mTeacherDownListener = new OnTeacherDownListener() {
        @Override
        public void onTeacherDown() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TEACHER_DOWN));
        }
    };

    /**
     * 老师离开的监听
     */
    private OnTeacherGoListener mTeacherGoListener = new OnTeacherGoListener() {
        @Override
        public void OnTeacherGo(boolean isGo) {

        }
    };

    /**
     * 房间计时器回调
     * <p>
     */
    private OnRoomTimerListener mRoomTimerListener = new OnRoomTimerListener() {
        @Override
        public void onTimer(long startTime, long lastTime) {//开始计时
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_TIMER_START, startTime, lastTime));
        }

        @Override
        public void onStop() {//停止计时
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_TIMER_STOP));
        }
    };
    /**
     * 答题回调
     * <p>
     */
    private OnRollCallListener mRollCallListener = new OnRollCallListener() {
        @Override
        public void onStart(Vote vote) {//开始答题
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_START, vote));
        }

        @Override
        public void onStop(String voteId) {//停止答题
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_STOP, voteId));
        }

        @Override
        public void onResult(VoteResult voteResult) {//答题结果
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_RESULT, voteResult));
        }
    };
    /**
     * 设置举手回调
     */
    private CCBarLeyManager.OnHandupListener mHandupListener = new CCBarLeyManager.OnHandupListener() {
        @Override
        public void onHandupStatus(String userid, boolean isHandup) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_HANDUP, userid, isHandup));
        }

    };
    /**
     * 设置锁定回调
     */
    private OnLockListener mLockListener = new OnLockListener() {
        @Override
        public void onLock(String userid, boolean isLock) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_LOCK, userid, isLock));
        }
    };
    /**
     * 老师拉学生上麦，而学生不具备上麦条件回调
     */
    private OnRecivePublishError mRecivePublishError = new OnRecivePublishError() {
        @Override
        public void onError(String userid, String username) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DEVICE_FAIL, userid, username));
        }
    };
    /**
     * 设置插播音视频回调
     */
    private OnInterludeMediaListener mInterludeMediaListener = new OnInterludeMediaListener() {
        @Override
        public void onInterlude(JSONObject object) {
            try {
                String handler = object.getString("handler");
                mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INTERLUDE_MEDIA, object));
            } catch (Exception ignored) {
            }
        }
    };
    /**
     * 设置视频放大到文档区回调方法
     */
    private OnVideoControlListener mVideoControlListener = new OnVideoControlListener() {

        @Override
        public void OnVideoControl(String userid, String type) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_VIDEO_CONTROL, userid, type));
        }
    };
    /**
     * 暖场视频取消，开始监听
     */
    private OnInterWramMediaListener mOnInterWramMediaListener = new OnInterWramMediaListener() {
        @Override
        public void onInterWram(Object object) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_WARM_VIDEO, object));
        }
    };

    /**
     * 监听当前文档
     */
//    private CCAtlasClient.OnPageChangeListener mOnPageChangeListener = new CCAtlasClient.OnPageChangeListener() {
//        @Override
//        public void onPageChange(int position, boolean issdk) {
//            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_PAGECHANGE, position, issdk));
//        }
//    };
    //    /**
//     * 流中断监听事件
//     */
//    private CCAtlasClient.OnPublishBeakOffListener mOnInterruptPublishListener = new CCAtlasClient.OnPublishBeakOffListener() {
//
//        @Override
//        public void onPublishBeakOff(JSONObject object) {
//            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_INTERRUPT_PUBLISH, object));
//        }
//
//        @Override
//        public void onNotifyPublish(String streamid, String userid) {
//            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_NOTIFY_PUBLISH,streamid,userid));
//        }
//    };

    /**
     * 监听头脑风暴事件
     */
    private OnBrainStomListener mBrainStomListener = new OnBrainStomListener() {
        @Override
        public void onStart(BrainStom brainStom) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_BRAIN_STOM, brainStom));
        }

        @Override
        public void onStop(String id) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_BRAIN_STOM_STOP, id));
        }
    };

    /**
     * 监听投票事件
     */
    private OnBallotListener mBallotListener = new OnBallotListener() {

        @Override
        public void onStart(Ballot ballot) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_BALLOT_START, ballot));
        }

        @Override
        public void onStop(String id) {

        }

        @Override
        public void onResult(BallotResult ballotResult) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_BALLOT_RESULT, ballotResult));
        }
    };
    //发送奖杯事件
    private OnSendCupListener mSendCupListener = new OnSendCupListener() {
        @Override
        public void onSendCup(SendReward sendReward) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SEND_CUP, sendReward));
        }
    };
    //发送鲜花事件
    private OnSendFlowerListener mSendFlowerListener = new OnSendFlowerListener() {
        @Override
        public void onSendFlower(SendReward sendReward) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SEND_FLOWER, sendReward));
        }
    };
    //发送鲜花事件
    private OnSendHammerListener mSendHammerListener = new OnSendHammerListener() {
        @Override
        public void onSendHammer(SendReward sendReward) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SEND_HAMMER, sendReward));
        }
    };

    //黑流检测
    private OnStreamStatsListener mOnStreamStatsListener = new OnStreamStatsListener() {
        @Override
        public void OnStreamStats(CCPublicStream ccPublicStream) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_START, ccPublicStream));
        }
    };
    //流服务断开通知
    private OnAtlasServerListener mAtlasServerDisconnectedListener = new OnAtlasServerListener() {
        @Override
        public void onAtlasServerDisconnected() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_ATLAS_SERVER_DISCONNECTED));
        }
    };

    //断网，推流中断通知
    private OnPublishStreamErrListener mOnPublishStreamErrListener = new OnPublishStreamErrListener() {
        @Override
        public void publishStreamErr(String streamId) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_PUBLISH_DISCONNECTED, streamId));
        }
    };
    //双师课堂隐藏连麦按钮监听事件
    private OnDoubleTeacherListener mDoubleTeacherListener = new OnDoubleTeacherListener() {
        @Override
        public void isDoubleTeacher(boolean flag) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_FLAG, flag));
        }
    };

    //处理双师课堂和小班课之间的授权标注处理
    private OnDoubleTeacherIsDrawListener mDoubleTeacherIsDrawListener = new OnDoubleTeacherIsDrawListener() {
        @Override
        public void DoubleTeacherIsDraw(boolean isAuth) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_AUTH, isAuth));
        }

        @Override
        public void DoubleTeacherIsSetUpTeacher(boolean isSetUpTeacher) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_TEACHER, isSetUpTeacher));
        }
    };

    //当前房间是不是需要取消音频
    private OnTalkerAudioStatusListener mOnTalkerAudioStatusListener = new OnTalkerAudioStatusListener() {
        @Override
        public void OnTalkerAudioStatus(int talkerAudio) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_TALKER_AUDIO_STATUS, talkerAudio));
        }
    };

    private OnSwitchSpeak mOnSwitchSpeak = new OnSwitchSpeak() {
        @Override
        public void OnSwitchSpeakOn() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SWITCH_SPEAK_ON));
        }

        @Override
        public void OnSwitchSpeakOff() {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SWITCH_SPEAK_OFF));
        }
    };

    /**
     * 插播视频同步
     */
    private OnMediaSyncListener mediaSyncListener = new OnMediaSyncListener(){
        @Override
        public void OnMediaSync(JSONObject object) {
            mEventBus.post(new MyEBEvent(Config.INTERACT_EVENT_WHAT_MEDIA_SYNC,object));
        }
    };

    public void addInteractListeners() {
        ccChatManager.setOnChatListener(mChatListener);
        mInteractSession.setOnUserCountUpdateListener(mUserCountUpdateListener);
        ccBarLeyManager.setOnUserListUpdateListener(mUserListUpdateListener);
        ccChatManager.setOnGagListener(mGagUpdateListener);
        ccDocViewManager.setOnTalkerAuthDocListener(mAuthDrawListener);
        mInteractSession.setOnMediaListener(mAudioListener);
        ccBarLeyManager.setOnQueueMaiUpdateListener(mQueueMaiUpdateListener);
        ccBarLeyManager.setOnNotifyMaiStatusLisnter(mNotifyMaiStatusLisnter);
        ccBarLeyManager.setOnKickOutListener(mKickOutListener);
        mInteractSession.setOnMediaModeUpdateListener(mMediaModeUpdateListener);
        ccBarLeyManager.setOnSpeakModeUpdateListener(mLianmaiModeUpdateListener);//连麦模式更新
        mInteractSession.setOnNotifyStreamListener(mNotifyStreamListener);
//        ccBarLeyManager.setOnNotifyStreamListener(mNotifyStreamListener);
        mInteractSession.setOnReceiveNamedListener(mStartRollCallListener);
        mInteractSession.setOnStartNamedListener(mRollCallListListener);
        mInteractSession.setOnAnswerNamedListener(mAnswerRollCallListener);
        mInteractSession.setOnServerListener(mServerDisconnectListener);
        ccBarLeyManager.setOnNotifyInviteListener(mNotifyInviteListener);
        mInteractSession.setOnClassStatusListener(mClassStatusListener);
        mInteractSession.setOnFollowUpdateListener(mFollowUpdateListener);
        mInteractSession.setOnTemplateTypeUpdateListener(mTemplateTypeUpdateListener);
        mInteractSession.setOnTeacherDownListener(mTeacherDownListener);
        mInteractSession.setOnRoomTimerListener(mRoomTimerListener);
        mInteractSession.setOnRollCallListener(mRollCallListener);
        ccBarLeyManager.setOnHandupListener(mHandupListener);
        mInteractSession.setOnLockListener(mLockListener);
        mInteractSession.setOnRecivePublishError(mRecivePublishError);
        mInteractSession.setOnInterludeMediaListener(mInterludeMediaListener);
        mInteractSession.setOnVideoControlListener(mVideoControlListener);
        mInteractSession.setOnInterWramMediaListener(mOnInterWramMediaListener);
//        mInteractSession.setOnPageChangeListener(mOnPageChangeListener);
//        mInteractSession.setOnInterruptPublishListener(mOnInterruptPublishListener);
        mInteractSession.setOnBrainStomListener(mBrainStomListener);
        mInteractSession.setOnBallotListener(mBallotListener);
        mInteractSession.setOnSendCupListener(mSendCupListener);
        mInteractSession.setOnSendFlowerListener(mSendFlowerListener);
        mInteractSession.setOnSendHammerListener(mSendHammerListener);
        mInteractSession.setOnStreamStatsListener(mOnStreamStatsListener);
        mInteractSession.setOnAtlasServerListener(mAtlasServerDisconnectedListener);
        mInteractSession.setOnPublishStreamErrListener(mOnPublishStreamErrListener);
        mInteractSession.setOnTalkerAudioStatusListener(mOnTalkerAudioStatusListener);
        mInteractSession.setOnSwitchSpeak(mOnSwitchSpeak);
        mInteractSession.setOnTeacherGoListener(mTeacherGoListener);
        mInteractSession.setOnMediaSyncListener(mediaSyncListener);
    }

    public void reset() {
        instance = null;
    }

    /**
     * 获取时间段
     *
     * @param time 时间
     * @return
     */
    private String getTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }


}
