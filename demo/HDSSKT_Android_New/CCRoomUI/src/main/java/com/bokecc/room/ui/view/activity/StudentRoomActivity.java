package com.bokecc.room.ui.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.dialog.BottomMenuDialog;
import com.bokecc.common.dialog.CustomTextViewDialog;
import com.bokecc.common.utils.AndroidBug5497Workaround;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.OnDisplayInteractionListener;
import com.bokecc.room.ui.model.ChatEntity;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.utils.UserComparator;
import com.bokecc.room.ui.view.dialog.BallotDialog;
import com.bokecc.room.ui.view.dialog.BallotResultDialog;
import com.bokecc.room.ui.view.dialog.BrainStomDialog;
import com.bokecc.room.ui.view.dialog.CallNameDialog;
import com.bokecc.room.ui.view.dialog.VoteDialog;
import com.bokecc.room.ui.view.dialog.VoteResultDialog;
import com.bokecc.room.ui.view.doc.CCDocView;
import com.bokecc.room.ui.view.menu.MenuBottomView;
import com.bokecc.room.ui.view.menu.MenuTopView;
import com.bokecc.room.ui.view.video.BaseVideoManager;
import com.bokecc.room.ui.view.video.listener.VideoViewListener;
import com.bokecc.room.ui.view.video.widget.InterCutVideoView;
import com.bokecc.room.ui.view.video.LectureVideoManager;
import com.bokecc.room.ui.view.video.MainVideoManager;
import com.bokecc.room.ui.view.video.widget.SuspensionVideoView;
import com.bokecc.room.ui.view.video.TileVideoManager;
import com.bokecc.room.ui.view.widget.CupView;
import com.bokecc.room.ui.view.widget.TimerWidget;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.sskt.base.bean.BallotResult;
import com.bokecc.sskt.base.bean.BrainStom;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.SendReward;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.bean.Vote;
import com.bokecc.sskt.base.bean.VoteResult;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.config.LogConfig;
import com.bokecc.sskt.base.common.util.CCStartBean;
import com.bokecc.sskt.base.common.util.LogUtil;
import com.bokecc.ccdocview.model.DocInfo;
import com.bokecc.stream.bean.CCStream;
import com.example.ccbarleylibrary.CCBarLeyCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_AUTO;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_FREE;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_NAMED;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_STATUS_IN_MAI;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_STATUS_UP_MAI;
import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_AUDIO;
import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_BOTH;
import static com.bokecc.sskt.base.CCAtlasClient.TALKER;

/**
 * 学生直播间
 *
 * @author wy
 */
@RuntimePermissions
public class StudentRoomActivity extends CCRoomActivity implements OnDisplayInteractionListener {

    private final String TAG = "StudentRoomActivity:";

    /**
     * 文档视图
     */
    private CCDocView ccDocView;

    /**
     * 视频管理类
     */
    private BaseVideoManager mVideoManager;


    /**
     * 屏幕共享控件
     */
    private SuspensionVideoView shareVideoView;
    /**
     * 辅助摄像头控件
     */
    private SuspensionVideoView assistVideoView;


    /**
     * 直播间状态视图
     */
    private LinearLayout student_room_status_tip;
    /**
     * 直播间状态提示语
     */
    private TextView student_room_status_tip_tv;

    /**
     * 奖杯视图
     */
    private CupView cupView;
    /**
     * 顶部菜单
     */
    private MenuTopView menuTopView;
    /**
     * 底部菜单
     */
    protected MenuBottomView menuBottomView;


    /**
     * 计时控件
     */
    private TimerWidget timerWidget;

    /**
     * 房间开启，自动连麦中，需要等待
     */
    private boolean needWait = true;

    protected static int mRole = TALKER;
    protected CallNameDialog mRollCallDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sClassDirection == 1) {
            AndroidBug5497Workaround.assistActivity(this);
        }
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        if (sClassDirection == 0) {
            return R.layout.activity_student_room_layout;
        } else {
            return R.layout.activity_student_room_layout_h;
        }
    }

    /**
     * 初始化视图
     */
    protected void initView() {
        //辅助摄像头
        assistVideoView = findViewById(R.id.student_assist_video);
        assistVideoView.setListener(new SuspensionVideoView.SuspensionVideoViewListener() {
            @Override
            public void fullScreen() {
                shareVideoView.setSVVVisibility(View.GONE);
                interCutVideoView.setICVVVisibility(View.GONE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.GONE);
                }
            }

            @Override
            public void exitFullScreen() {
                shareVideoView.setSVVVisibility(View.VISIBLE);
                interCutVideoView.setICVVVisibility(View.VISIBLE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.VISIBLE);
                }
            }
        });

        //共享屏幕
        shareVideoView = findViewById(R.id.student_share_video);
        shareVideoView.setListener(new SuspensionVideoView.SuspensionVideoViewListener() {
            @Override
            public void fullScreen() {
                assistVideoView.setSVVVisibility(View.GONE);
                interCutVideoView.setICVVVisibility(View.GONE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.GONE);
                }
            }

            @Override
            public void exitFullScreen() {
                assistVideoView.setSVVVisibility(View.VISIBLE);
                interCutVideoView.setICVVVisibility(View.VISIBLE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.VISIBLE);
                }
            }
        });

        //插播音视频控件
        interCutVideoView = findViewById(R.id.student_remote_video_container);
        interCutVideoView.setVideoEventListener(new InterCutVideoView.VideoEventListener() {
            @Override
            public int getVideoCurrentTime() {
                try {
                    return (int) (mCCAtlasClient.getInteractBean().getVideo().getDouble("current_time") * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public int getAudioCurrentTime() {
                try {
                    return (int) (mCCAtlasClient.getInteractBean().getAudio().getDouble("current_time") * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public void fullScreen() {
                assistVideoView.setSVVVisibility(View.GONE);
                shareVideoView.setSVVVisibility(View.GONE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.GONE);
                }
            }

            @Override
            public void exitFullScreen() {
                assistVideoView.setSVVVisibility(View.VISIBLE);
                shareVideoView.setSVVVisibility(View.VISIBLE);
                if (mVideoManager != null) {
                    mVideoManager.setRecyclerViewVisibility(View.VISIBLE);
                }
            }
        });

        //房间提示
        student_room_status_tip = findViewById(R.id.student_room_status_tip);
        student_room_status_tip_tv = findViewById(R.id.student_room_status_tip_tv);

        //暖场视频
        warmUpVideoView = findViewById(R.id.student_warm_up_video);
        warmUpVideoView.setPortrait(sClassDirection == 0);
        //奖杯视图
        cupView = findViewById(R.id.room_cup_view);

        //顶部菜单
        menuTopView = findViewById(R.id.menu_top_rl);
        menuTopView.setRoomName(mCCAtlasClient.getRoom() != null ? mCCAtlasClient.getRoom().getRoomName() : "");
        menuTopView.setListener(new MenuTopView.MenuTopListener() {

            @Override
            public ArrayList<CCUser> getUserList() {
                return mCCAtlasClient.getUserList();
            }

            @Override
            public void closeRoom() {
                showExitDialog("是否确认离开课堂？");
            }

            @Override
            public void videoController(boolean isVideoShow) {
                mVideoManager.setRecyclerViewVisibility(isVideoShow ? View.VISIBLE : View.GONE);

            }

            @Override
            public void videoFollow() {

            }

            @Override
            public void onClickUser(CCUser user, int position) {

            }
        });


        //底部菜单
        menuBottomView = findViewById(R.id.menu_bottom_rl);
        menuBottomView.setListener(this, new MenuBottomView.MenuBottomListener() {

            @Override
            public void menuOpenChat() {
                if (mCCChatManager.isRoomGag() || mCCChatManager.isGag()) {
                    Tools.showToast("禁言中");
                } else {
                    chatView.openChat();
                }
            }

            @Override
            public void menuLianmai() {
                lianmai();
            }

            @Override
            public void handup() {
                mCCBarLeyManager.Studenthandup(!isAutoHandup, new CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isAutoHandup = !isAutoHandup;
                        menuBottomView.setHandupBtn(isAutoHandup);
                    }

                    @Override
                    public void onFailure(String err) {
                        Tools.showToast(err);
                    }
                });
            }

        });


        //聊天发送
        setChatView(R.id.room_chat_cv);
        //计时控件
        timerWidget = findViewById(R.id.id_student_timer);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 是否开播
        if (!mCCAtlasClient.isRoomLive()) {
            startLiveed = false;
            warmUpVideoView.hideView();
            student_room_status_tip.setVisibility(View.VISIBLE);
            mCCAtlasClient.getWarmVideoUrl(new CCAtlasCallBack() {
                @Override
                public void onSuccess(Object url) {
                    if (url == null) {
                        return;
                    }
                    mAppPlayUrl = url.toString();
                    //判断是否有暖场视频
                    if (!TextUtils.isEmpty(mAppPlayUrl)) {
                        warmUpVideoView.initMediaPlayer(mAppPlayUrl);
                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    LogUtil.e(TAG, "errCode:" + errCode + ",  errMsg:" + errMsg);
                }
            });

        } else {
            startLiveed = true;
            //隐藏分辨率按钮
            menuBottomView.setSettingBtnVisibility(View.GONE);
            //隐藏未开始提示
            student_room_status_tip.setVisibility(View.GONE);
            initVideoViewManager();
            if (sClassDirection == 1 && mVideoManager instanceof LectureVideoManager)
                menuTopView.setVideoControllerShown(true);
            //开始进来首先获取流对象，直接去订阅
            mCCAtlasClient.setSubscribeRemoteStreams();
            setChatViewBg();
        }

        // 如果有插播的多媒体 进行播放器初始化
        try {
            if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().hasMedia()) {
                if (mCCAtlasClient.getInteractBean().getAudio() != null) {
                    boolean isStatus = mCCAtlasClient.getInteractBean().getAudio().getString("status").equals("1");
                    String src = mCCAtlasClient.getInteractBean().getAudio().getString("src");
                    interCutVideoView.initStatus(false, isStatus, src);
                }
                if (mCCAtlasClient.getInteractBean().getVideo() != null) {
                    boolean isStatus = mCCAtlasClient.getInteractBean().getVideo().getString("status").equals("1");
                    String src = mCCAtlasClient.getInteractBean().getVideo().getString("src");
                    interCutVideoView.initStatus(true, isStatus, src);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //禁言状态
        if (mCCChatManager.isRoomGag() || mCCChatManager.isGag()) {
            menuBottomView.setChatBtnMute(true);
        }

        //连麦模式
        try {
            if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_FREE) {
                menuBottomView.setLianmaiStatus(0);
            } else if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_NAMED) {
                menuBottomView.setLianmaiStatus(3);
            } else if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                menuBottomView.setLianmaiBtnVisibility(View.GONE);
                menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                if (mCCAtlasClient.isRoomLive()) {
                    needWait = false;
                    //加个延迟处理，要不然会出现后台更新不及时导致的用户不存在的问题。
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            lianmai();
                        }
                    }, 3000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化视频模式管理类
     */
    private void initVideoViewManager() {
        try {
            //如果以初始化，不支持改变（后续再支持）
            if (mVideoManager != null) {
                mVideoManager.setVisibility(View.VISIBLE);
                return;
            }
            int type = mCCAtlasClient.getInteractBean().getTemplate();
            switch (type) {
                case CCAtlasClient.TEMPLATE_SPEAK:

                    mVideoManager = new LectureVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_lecture_layout));

                    mVideoManager.setListener(new VideoViewListener() {
                        @Override
                        public void fullScreen() {
                            assistVideoView.setSVVVisibility(View.GONE);
                            shareVideoView.setSVVVisibility(View.GONE);
                            interCutVideoView.setICVVVisibility(View.GONE);
                        }

                        @Override
                        public void exitFullScreen() {
                            assistVideoView.setSVVVisibility(View.VISIBLE);
                            shareVideoView.setSVVVisibility(View.VISIBLE);
                            interCutVideoView.setICVVVisibility(View.VISIBLE);
                        }

                        @Override
                        public VideoStreamView getMySelfVideoStreamView() {
                            return mSelfStreamView;
                        }

                        @Override
                        public void onClickDocVideo() {
                            toggleTopAndBottom();
                        }
                    });
                    initDocView();
                    ((LectureVideoManager) mVideoManager).setCCDocView(ccDocView);
                    break;
                case CCAtlasClient.TEMPLATE_SINGLE:
                    mVideoManager = new MainVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_main_layout));
                    ((MainVideoManager) mVideoManager).setOnDisplayInterListener(this);
                    break;
                case CCAtlasClient.TEMPLATE_TILE:
                    mVideoManager = new TileVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_tile_layout));
                    ((TileVideoManager) mVideoManager).setOnDisplayInterListener(this);
                    break;
                case CCAtlasClient.TEMPLATE_DOUBLE_TEACHER:
                    mVideoManager = new MainVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_main_layout));
                    ((MainVideoManager) mVideoManager).setOnDisplayInterListener(this);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化文档视图
     */
    private void initDocView() {
        if (ccDocView == null) {
            ccDocView = findViewById(R.id.id_lecture_video_doc);
            ccDocView.initRole(this, CCAtlasClient.TALKER, sClassDirection);
//            ccDocView.setDocBackgroundColor("#cfe2f3");
            ccDocView.setDocHandleListener(new CCDocView.IDocHandleListener() {
                @Override
                public void exitDocFullScreen() {
                    chatView.setVisibility(View.VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    menuTopView.setVisibility(View.VISIBLE);
                    menuBottomView.setVisibility(View.VISIBLE);
                    ((LectureVideoManager) mVideoManager).exitDocFullScreen();
                }

                @Override
                public void docFullScreen() {
                    chatView.setVisibility(View.GONE);
                    menuTopView.removeAnimation();
                    menuTopView.setVisibility(View.GONE);
                    menuBottomView.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                @Override
                public void showActionBar() {
                    toggleTopAndBottom();
                }

                @Override
                public void onClickDocView(View view) {
                    toggleTopAndBottom();
                }

                @Override
                public void onMenuShowAnimStart() {
                    menuTopView.startShowAnim();
                }

                @Override
                public void onMenuHideAnimStart() {
                    menuTopView.startHideAnim();
                }
            });
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onInteractEvent(MyEBEvent event) {
        try {
            if (event.what != Config.INTERACT_EVENT_WHAT_USER_COUNT) {
                Tools.log(LogConfig.onInteractEventLog, "" + Tools.intToHex(event.what));
            }

            switch (event.what) {
                case Config.INTERACT_EVENT_WHAT_SERVER_CONNECT://直播间连接
                    if (mCCAtlasClient.getInteractBean() != null
                            && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO
                            && mCCAtlasClient.isRoomLive()) {
                        if (needWait) {
                            lianmai();
                        }
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT://直播间断开
                    showExitDialogOneBtn("当前用户掉线了");
                    break;
                case Config.INTERACT_EVENT_WHAT_CHAT://聊天信息更新
                    chatView.updateChatList((ChatEntity) event.obj);
                    setChatViewBg();
                    break;
                case Config.INTERACT_EVENT_WHAT_ERROR://异常通知
                    Tools.showToast((String) event.obj);
                case Config.INTERACT_EVENT_WHAT_USER_COUNT://更新房间人数
                    int mCount = (Integer) event.obj + (Integer) event.obj2;
                    menuTopView.setUserCount(mCount);
                    break;
                case Config.INTERACT_EVENT_WHAT_USER_GAG://被禁言监听事件
                    updateChatStatus((String) event.obj, (Boolean) event.obj2);
                    break;
                case Config.INTERACT_EVENT_WHAT_ALL_GAG://全体禁言
                    updateChatStatus("", (Boolean) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_INVITE://学生收到老师上麦邀请回调
                    showInvite();
                    break;
                case Config.INTERACT_EVENT_WHAT_INVITE_CANCEL://学生收到老师取消邀请连接事件
                    dismissInvite();
                    break;
                case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START://开始上课事件
                    startClass();
                    break;
                case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP://下课事件
                    endClass();
                    break;
                case Config.INTERACT_EVENT_WHAT_UP_MAI://连麦成功
                    publish();
                    break;
                case Config.INTERACT_EVENT_WHAT_DOWN_MAI://下麦成功
                    unpublish();
                    break;
                case Config.INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE://连麦多媒体模式变化回调
                    if (mMaiStatus == 3) { // 当前用户在连麦中
                        if ((int) event.obj == MEDIA_MODE_BOTH) {
                            mCCAtlasClient.enableVideo(true);
                        } else {
                            mCCAtlasClient.disableVideo(true);
                        }
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE://连麦模式更新回调
                    int mode = (int) (event).obj;
                    menuBottomView.setLianmaiBtnVisibility(View.VISIBLE);
                    if (mode == LIANMAI_MODE_AUTO) {
                        menuBottomView.setLianmaiStatus(3);
                        if (mMaiStatus == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
                            updateMaiButton(CCAtlasClient.LIANMAI_STATUS_MAI_ING);
                        } else {
                            menuBottomView.setLianmaiBtnVisibility(View.GONE);
                            menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                        }
                    } else {
                        menuBottomView.setLianmaiStatus(0);
                        if (mode == LIANMAI_MODE_FREE) { // 切换到自由连麦 更新左上角举手标识
                            if (isNamedHandup) {
                                // 更新连麦按钮
                                sortUser(mCCAtlasClient.getUserList());
                            }
//                        updateHandUpFlag(false);//todo 老师端显示举手图标
                        }
                        // 更新连麦按钮
                        updateMaiButton(mMaiStatus);
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_QUEUE_MAI://排麦的回调
                    ArrayList<CCUser> users = (ArrayList<CCUser>) event.obj; // 重新赋值
                    if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_NAMED) {
                        // 点名连麦模式
                        boolean flag = false;
                        for (CCUser user : users) {
                            if (user.getLianmaiStatus() == LIANMAI_STATUS_IN_MAI) {
                                flag = true;
                                break;
                            }
                        }
                        //todo
//                    if (flag != isNamedHandup)
//                        updateHandUpFlag(flag);//连麦举手
                    } else { // 自由连麦模式
                        if (mMaiStatus == 1) { // 如果麦序发生变化 并且当前用户在麦序中 进行麦序计算
                            sortUser(users);
                        }
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_HANDUP://设置举手回调
                    String userid = (String) event.obj;
                    if (userid.equals(mCCAtlasClient.getUserIdInPusher())) {
                        isAutoHandup = (boolean) event.obj2;
                        menuBottomView.setHandupBtn(isAutoHandup);
                    } else {
                        //todo 老师端显示有人举手
//                    updateHandUpFlag((boolean) event.obj2);
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_ADDED://添加流
                    try {
                        final SubscribeRemoteStream stream = (SubscribeRemoteStream) event.obj;
                        if (stream.getRemoteStream().isScreenStream()) {//桌面共享
                            mCCAtlasClient.SubscribeStream(stream.getRemoteStream(), com.bokecc.stream.config.Config.RENDER_MODE_FIT, new CCAtlasCallBack<CCStream>() {
                                @Override
                                public void onSuccess(final CCStream ccstream) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shareVideoView.showScreen(stream, ccstream);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int errCode, String errMsg) {
                                    Tools.showToast(errMsg);
                                }
                            });
                        } else if (stream.getRemoteStream().getHasImprove()) {//辅助摄像头
                            mCCAtlasClient.SubscribeStream(stream.getRemoteStream(), com.bokecc.stream.config.Config.RENDER_MODE_FIT, new CCAtlasCallBack<CCStream>() {
                                @Override
                                public void onSuccess(final CCStream ccstream) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            assistVideoView.showScreen(stream, ccstream);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int errCode, String errMsg) {
                                    Tools.showToast(errMsg);
                                }
                            });
                        } else {//普通流
                            SubscribeRemoteStream streamTemp = (SubscribeRemoteStream) event.obj;
                            Tools.log(LogConfig.ADDVIDEOVIEW, "1.onInteractEvent:" + streamTemp.getUserId() + ":" + streamTemp.getUserName() + ":" + streamTemp.getUserRole() + ":" + streamTemp.getStreamId());
                            if (mVideoManager == null) {
                                initVideoViewManager();
                            }
                            mVideoManager.addStreamView(streamTemp);
                        }

                        EventBus.getDefault().removeStickyEvent(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_REMOVED://移除流回调
                    try {
                        final SubscribeRemoteStream stream = (SubscribeRemoteStream) event.obj;
                        if (stream.getRemoteStream().isScreenStream()) {
                            try {
                                mCCAtlasClient.unSubscribeStream(stream.getRemoteStream(), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                shareVideoView.dismissScreen(stream);
                            }
                            return;
                        } else if (stream.getRemoteStream().getHasImprove()) {
                            try {
                                mCCAtlasClient.unSubscribeStream(stream.getRemoteStream(), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                assistVideoView.dismissScreen(stream);
                            }
                            return;
                        } else {
                            mVideoManager.removeStreamView((SubscribeRemoteStream) event.obj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_ERROR:
                    unpublish();
                    break;
                case Config.INTERACT_EVENT_WHAT_START_NAMED://开始点名
                    showCallNamed((Integer) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_START://答题卡
                    showVote((Vote) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_STOP://停止答题
                    dismissVote();
                    break;
                case Config.INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_RESULT://答题结果
                    showVoteResult((VoteResult) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_BRAIN_STOM://头脑风暴
                    showBrainStom((BrainStom) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_BRAIN_STOM_STOP://停止头脑风暴
                    dismissBrainStom();
                    break;
                case Config.INTERACT_EVENT_WHAT_BALLOT_START://开启投票
                    showBallot((Ballot) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_BALLOT_RESULT://投票结果
                    showBallotResult((BallotResult) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_ROOM_TIMER_START://开始计时
                    timerWidget.showTimer((long) event.obj, (long) event.obj2);
                    break;
                case Config.INTERACT_EVENT_WHAT_ROOM_TIMER_STOP://停止计时
                    timerWidget.dismissTimer();
                    break;
                case Config.INTERACT_EVENT_WHAT_USER_AUDIO://学生麦克风摄像头状态被动变化监听事件
                    if (mVideoManager != null)
                        mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, (Boolean) event.obj3, 0);
                    break;
                case Config.INTERACT_EVENT_WHAT_USER_VIDEO://学生摄像头状态被动变化监听事件
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, (Boolean) event.obj3, 1);
                    break;
                case Config.INTERACT_EVENT_WHAT_LOCK://设置锁定回调
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, mCCAtlasClient.getUserIdInPusher().equals(event.obj), 3);
                    break;
                case Config.INTERACT_EVENT_WHAT_AUTH_DRAW:
                    if (ccDocView != null) {
                        ccDocView.authDrawOrSetupTeacher((String) event.obj, (Boolean) event.obj2, 2);
                    }
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, false, 2);
                    break;
                case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER:
                    if (ccDocView != null) {
                        ccDocView.authDrawOrSetupTeacher((String) event.obj, (Boolean) event.obj2, 4);
                    }
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, false, 4);
                    break;
                case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER_PAGE:
                    if (ccDocView != null) {
                        ccDocView.setDocInfo((DocInfo) event.obj, (int) event.obj2, 0);
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_DOC_CHANGE:
                    Tools.log(TAG, "======INTERACT_EVENT_WHAT_DOC_CHANGE=========");
                    if (ccDocView != null) {
                        ccDocView.setupTeacherFlag((DocInfo) event.obj, (int) event.obj2);
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_INTERLUDE_MEDIA://插播视频
                    interCutVideoView.startInterludeMedia((JSONObject) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_MEDIA_SYNC://插播音视频的进度回调
                    interCutVideoView.syncMediaTime((JSONObject) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_VIDEO_CONTROL://视频放到文档区域显示
                    String streamId = (String) event.obj;
                    String VideoType = (String) event.obj2;
                    mVideoManager.setVideoToDoc(streamId, VideoType);
                    break;
                case Config.INTERACT_EVENT_WHAT_MAIN_VIDEO_FOLLOW://主视频模式下，主视频更新回调
                    if (mVideoManager != null) {
                        mVideoManager.switchMainVideo((String) event.obj);
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_KICK_OUT:
                    isExit = true;
                    isKick = true;
                    showExitDialogOneBtn("对不起，您已经被踢出该直播间");
                    break;
                case Config.INTERACT_EVENT_WHAT_FORCE_OUT:
                    showExitDialogOneBtn("对不起，您已经被挤出该直播间");
                    break;
                case Config.INTERACT_EVENT_WHAT_SEND_CUP://发奖杯
                    SendReward cup = (SendReward) event.obj;
                    setOnUserCupCountListener(new OnUserCupCountListener() {
                        @Override
                        public void getUserCupCount(ArrayList<CCUser> users) {
                            menuTopView.setUserCupCount(users);
                        }
                    });
                    upDataCup();
                    if (mCCAtlasClient.getInteractBean() != null && !mCCAtlasClient.getInteractBean().getUserId().equals(cup.getUserId())) {
                        String username = cup.getUserName();
                        cupView.startRewardAnim(username);
                    } else {
                        cupView.startRewardAnim("");
                    }
                    break;
                case Config.INTERACT_EVENT_WHAT_ATLAS_SERVER_DISCONNECTED:
//                    Tools.showToast("流服务断开");
//                    updateList4Unpublish();
                    showExitDialogOneBtn("网络已经断开，已经连麦的自动下麦！");
                    break;
                case Config.INTERACT_EVENT_WHAT_PUBLISH_DISCONNECTED:
                    Tools.showToast("断网，推流中断:  " + event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_FLAG://双师课堂隐藏连麦按钮监听事件
//                if ((Boolean) event.obj) {
//                    mLianmaiStyle.setVisibility(View.GONE);
//                    mHandup.setVisibility(View.GONE);
//                } else {
//                    mLianmaiStyle.setVisibility(View.VISIBLE);
//                    if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_FREE) {
//                        mLianmaiStyle.setBackgroundResource(R.drawable.queue_mai_selector);
//                    }
//                    mHandup.setVisibility(View.VISIBLE);
//                }
                    break;
                case Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_AUTH://处理双师课堂和小班课之间的授权标注处理
//                upDoubleTeacherIsAuth((Boolean) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_TEACHER://处理双师课堂和小班课之间的授权标注处理
//                upDoubleTeacherIsTeacher((Boolean) event.obj);
                    break;
                case Config.INTERACT_EVENT_WHAT_TALKER_AUDIO_STATUS://取消音频
                    int status = (int) event.obj;
                    CopyOnWriteArrayList<SubscribeRemoteStream> otherStreams = mCCAtlasClient.getSubscribeRemoteStreams();
                    for (SubscribeRemoteStream streamTemp : otherStreams) {
                        if (streamTemp.getUserRole() == TALKER && status == 0) {
                            mCCAtlasClient.pauseAudio(streamTemp.getRemoteStream(), null);
                        } else if (streamTemp.getUserRole() == TALKER && status == 1) {
                            mCCAtlasClient.playAudio(streamTemp.getRemoteStream(), null);
                        }
                    }
                    break;
//            case Config.INTERACT_EVENT_WHAT_WARM_VIDEO://暖场视频取消
//                //socket监听暖场视频已关闭 warm_close/warm_open
//                String action = (String) event.obj;
//                if ("warm_close".equals(action)) {
//                    isWarmVideoClosed = true;
//                } else {
//                    isWarmVideoClosed = false;
//                }
//                break;
////            case Config.INTERACT_EVENT_WHAT_TEACHER_DOWN:
////                break;
////            case Config.INTERACT_EVENT_WHAT_NOTIFY_PUBLISH:
////                Log.i(TAG, "onInteractEvent: " + (String)event.obj + "----->" + (String)event
////                        .obj2 + "----->2" + mSelfStreamView.getStream().getUserId());
////                mInteractSession.rePublishEvent((String)event.obj,(String)event
////                        .obj2,mSelfStreamView.getStream().getUserId());
////                break;
////            case Config.INTERACT_EVENT_WHAT_INTERRUPT_PUBLISH:
////                try {
////                    JSONObject republish = (JSONObject) event.obj;
////                    String SelfUserid = republish.getString("userid");
////                    String SelfRoomid = republish.getString("streamid");
////                    if(mSelfStreamView.getStream().getUserId().equals(SelfUserid)){
////                        Log.i(TAG, "wdh----->1onInteractEvent: " + SelfUserid + "----" +
////                                SelfRoomid);
////                        publish();
////                    }
////                }catch (JSONException e) {
////                    Log.e(TAG, "中断推流数据解析异常: [ " + e.getMessage() + " ]");
////                }
////                break;
                //            case Config.INTERACT_EVENT_WHAT_PAGECHANGE:
//                ((LectureFragment) mCurFragment).setPageChange((int) event.obj, (Boolean) event.obj2);
//                break;
//            case Config.INTERACT_EVENT_WHAT_TEMPLATE://模板更新回调，此版本不支持
//                setSelected((Integer) event.obj);
//                break;
//            case Config.INTERACT_EVENT_WHAT_SEND_FLOWER://发送鲜花，暂时不支持
//                SendReward flowerData = (SendReward) event.obj;
//                setFlowerAnimaImage(flowerData);
//                setFlowerAnimaText(flowerData);
//                mChatLayout.bringToFront();
//                break;
//            case Config.INTERACT_EVENT_WHAT_STREAM_START:
////                upDataStreamStats((CCPublicStream) event.obj);
//                break;
                case Config.INTERACT_EVENT_WHAT_STREAM_SWITCH_STUDENT_DOWNMAI://学生端下麦
                    handDownMai();//下麦
                    unpublish();//停止推流
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_SWITCH_ERROR:
                    showExitDialogOneBtn("优化线路失败，请重新进入房间！");
                    break;
            }
        } catch (Exception e) {
            Tools.handleException(e);
        }
    }

    /**
     * 上麦邀请对话框
     */
    private CustomTextViewDialog mInviteDialog;

    /**
     * 收到上麦邀请
     */
    protected void showInvite() {
        dismissInvite();
        mInviteDialog = new CustomTextViewDialog(this);
        mInviteDialog.setMessage("老师正邀请你连麦");
        mInviteDialog.setBtn("接受", "拒绝", new CustomTextViewDialog.CustomClickListener() {
            @Override
            public void onLeftClickListener() {
                showLoading();
                mCCBarLeyManager.acceptTeacherInvite(new CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        Tools.showToast(err);
                    }
                });
            }

            @Override
            public void onRightClickListener() {
                showLoading();
                mCCBarLeyManager.refuseTeacherInvite(new CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        Tools.showToast(err);
                    }
                });
            }

            @Override
            public void onOneBtnClickListener() {

            }
        });
    }

    /**
     * 取消上麦邀请
     */
    private void dismissInvite() {
        if (mInviteDialog != null && mInviteDialog.isShowing()) {
            mInviteDialog.dismiss();
            mInviteDialog = null;
        }
    }

    /**
     * 开始直播
     */
    private boolean startLiveed = false;

    /**
     * 开始上课
     */
    private void startClass() {
        Tools.log(TAG, "startClass:");
        if (startLiveed) {
            return;
        }

        startLiveed = true;

        //隐藏未开始直播视图
        student_room_status_tip.setVisibility(View.GONE);
        //隐藏分辨率按钮
        menuBottomView.setSettingBtnVisibility(View.GONE);
        //开启直播，关闭暖场
        warmUpVideoView.closeVideo();

        //开启连麦
        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
            startLianmai();
        }

        initVideoViewManager();
        if (ccDocView != null) {
            ccDocView.startClass();
        }
        setChatViewBg();
        if (sClassDirection == 1 && mVideoManager instanceof LectureVideoManager)
            menuTopView.setVideoControllerShown(true);
    }

    /**
     * 结束上课
     */
    private void endClass() {
        Tools.log(TAG, "endClass:");
        startLiveed = false;
        //隐藏视图
        if (mVideoManager != null) {
            mVideoManager.setVisibility(View.INVISIBLE);
        }
        menuTopView.removeAnimation();
        menuBottomView.removeAnimation();
        menuTopView.postDelayed(new Runnable() {
            @Override
            public void run() {
                menuTopView.setVisibility(View.VISIBLE);
                menuBottomView.setVisibility(View.VISIBLE);
            }
        }, 255);

        updateMaiButton(MAI_STATUS_NORMAL);
        if (ccDocView != null) {
            ccDocView.stopClass();
        }
        if (sClassDirection == 1) {
            chatView.setVisibility(View.VISIBLE);
            setChatViewBg();
        } else {
            if (ccDocView != null) {
                ccDocView.docExitFullScreen();
            }
        }
        //显示未开始直播视图
        student_room_status_tip.setVisibility(View.VISIBLE);
        //显示分辨率按钮
        menuBottomView.setSettingBtnVisibility(View.VISIBLE);
        if (sClassDirection == 1 && mVideoManager instanceof LectureVideoManager)
            menuTopView.setVideoControllerShown(false);
    }


    /**
     * 更新聊天禁言
     *
     * @param userid
     * @param isAllowChat
     */
    private void updateChatStatus(String userid, final boolean isAllowChat) {
        if (TextUtils.isEmpty(userid)) {
            Tools.showToast(isAllowChat ? "老师关闭全体禁言" : "老师开启全体禁言");
            if (!isAllowChat) {
                menuBottomView.setChatBtnMute(true);
            } else {
                if (mCCChatManager.isGag()) {
                    menuBottomView.setChatBtnMute(true);
                } else {
                    menuBottomView.setChatBtnMute(false);
                }
            }
            return;
        }
        if (mCCAtlasClient.getUserIdInPusher().equals(userid)) {
            Tools.showToast(isAllowChat ? "您被老师关闭禁言" : "您被老师开启禁言");
            if (!isAllowChat) {
                menuBottomView.setChatBtnMute(true);
            } else {
                if (mCCChatManager.isRoomGag()) {
                    menuBottomView.setChatBtnMute(true);
                } else {
                    menuBottomView.setChatBtnMute(false);
                }
            }
        }
    }


    /**
     * 推流渲染器
     */
    private SurfaceView mSelfRenderer;
    /**
     * 推流视频信息
     */
    private VideoStreamView mSelfStreamView = new VideoStreamView();
    /**
     * 自动连麦模式 举手
     */
    private boolean isAutoHandup = false;
    /**
     * 点名连麦模式 举手
     */
    private boolean isNamedHandup = false;
    /**
     * 是否退出
     */
    protected boolean isExit = false;

    /**
     * 推流
     */
    private synchronized void publish() {
        try {
            if (mCCAtlasClient.getInteractBean() == null) {
                return;
            }

            //设置摄像头和音频的开启权限
            if (mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
                mCCAtlasClient.getInteractBean().getUserSetting().setAllowAudio(true);
                mCCAtlasClient.getInteractBean().getUserSetting().setAllowVideo(true);
            }

            //初始化
            SubscribeRemoteStream selfStream = new SubscribeRemoteStream();
            selfStream.setUserName(mCCAtlasClient.getInteractBean().getUserName());
            selfStream.setUserId(mCCAtlasClient.getUserIdInPusher());
            selfStream.setUserRole(mRole);
            selfStream.setAllowAudio(mCCAtlasClient.getInteractBean().getUserSetting().isAllowAudio());
            selfStream.setAllowVideo(mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo());
            selfStream.setLock(mCCAtlasClient.getInteractBean().isLock());
            selfStream.setAllowDraw(mCCAtlasClient.getInteractBean().getUserSetting().isAllowDraw());
            selfStream.setSetupTeacher(mCCAtlasClient.getInteractBean().getUserSetting().isSetupTeacher());
            mSelfStreamView.setStream(selfStream);

//            //设置基础参数
//            //设置开启照相机
//            mCCAtlasClient.setCameraType(true);
//            //设置推流分辨率
//            mCCAtlasClient.setResolution(SPUtil.getIntsance().getInt(MenuBottomView.STUDENT_RESOLUTION, MenuBottomView.Resolution_480P));
//            //设置图像显示方向
//            mCCAtlasClient.setAppOrientation(sClassDirection == 0);//true竖屏
//            //初始化本地流
//            mCCAtlasClient.createLocalStream(this, mCCAtlasClient.getMediaMode(), true);
//            //渲染流数据
//            mCCAtlasClient.attachLocalCameraStram(mSelfRenderer);


            //设置图像显示方向
            mCCAtlasClient.setAppOrientation(sClassDirection == 0);
            //设置分辨率
            mCCAtlasClient.setResolution(SPUtil.getIntsance().getInt(SettingActivity.STUDENT_RESOLUTION, mCCAtlasClient.getDefaultResolution()));
            //获取预览视图
            SurfaceView surfaceView = mCCAtlasClient.startPreview(this, com.bokecc.stream.config.Config.RENDER_MODE_FIT);
            mSelfStreamView.setSurfaceViewList(surfaceView);
            //添加到视频列表中
            mVideoManager.addVideoView(mSelfStreamView);

            //开启推流
            mCCAtlasClient.publish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    updateMaiButton(MAI_STATUS_ING);

                    if (!mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
                        mCCAtlasClient.disableAudio(true);
                    } else if (mCCAtlasClient.getInteractBean().getMediaMode() == MEDIA_MODE_AUDIO) {
                        mCCAtlasClient.enableVideo(false);
                        mCCAtlasClient.enableAudio(true);
                    } else if (mCCAtlasClient.getInteractBean().getMediaMode() == MEDIA_MODE_BOTH) {
                        mCCAtlasClient.enableVideo(true);
                        mCCAtlasClient.enableAudio(true);
                    }
                    if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                        if (isAutoHandup) {
                            mCCBarLeyManager.Studenthandup(!isAutoHandup, new CCBarLeyCallBack<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isAutoHandup = !isAutoHandup;
                                    menuBottomView.setHandupBtn(isAutoHandup);
                                }

                                @Override
                                public void onFailure(String err) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(int errCode, final String errMsg) {
                    try{
                        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                            menuBottomView.setLianmaiBtnVisibility(View.GONE);
                            menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                        } else {
                            menuBottomView.setHandupBtnVisibility(View.GONE);
                        }
                        mVideoManager.removeVideoView(mSelfStreamView);

                        Tools.showToast("推流失败：" + errMsg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Tools.showToast("推流报错：" + e.getMessage());
        }
    }

    /**
     * 结束推流
     */
    private synchronized void unpublish() {
        mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateList4Unpublish();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.showToast(errMsg);
                updateList4Unpublish();
            }
        });
    }

    /**
     * 更新界面
     */
    private void updateList4Unpublish() {
        updateMaiButton(MAI_STATUS_NORMAL);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isExit && mVideoManager != null) {
                    mVideoManager.removeVideoView(mSelfStreamView);//从视图区移除视图
                }
            }
        });
    }

    /**
     * 用户排序
     *
     * @param users
     */
    private void sortUser(ArrayList<CCUser> users) {
        ArrayList<CCUser> compareUsers = new ArrayList<>();
        for (CCUser user : users) {
            if (user.getLianmaiStatus() == LIANMAI_STATUS_IN_MAI ||
                    user.getLianmaiStatus() == LIANMAI_STATUS_UP_MAI) {
                compareUsers.add(user);
            }
        }
        Collections.sort(compareUsers, new UserComparator());
        mQueueIndex = 1;
        for (CCUser user : compareUsers) {
            if (user.getUserId().equals(mCCAtlasClient.getUserIdInPusher())) {
                updateMaiButton(MAI_STATUS_QUEUE);
                break;
            }
            mQueueIndex++;
        }
    }

    /**
     * 排麦状态
     */
    private int mQueueIndex;
    /**
     * 默认状态
     */
    private final int MAI_STATUS_NORMAL = 0;
    /**
     * 排麦中
     */
    private final int MAI_STATUS_QUEUE = 1;
    /**
     * 已连麦
     */
    private final int MAI_STATUS_ING = 2;
    /***/
    private boolean haveDownMai = true;
    /**
     * 连麦状态
     */
    private int mMaiStatus = 0;

    /**
     * 连麦
     */
    private void lianmai() {
        try {
            if (mMaiStatus == MAI_STATUS_NORMAL) {
                StudentRoomActivityPermissionsDispatcher.doRequestMaiWithPermissionCheck(this);
            } else if (mMaiStatus == MAI_STATUS_ING) {
                List<String> menuText = new ArrayList<>();
                menuText.add("切换摄像头");

                if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo()) {
                    menuText.add("关闭摄像头");
                } else {
                    menuText.add("开启摄像头");
                }
                if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getUserSetting().isAllowAudio()) {
                    menuText.add("关闭麦克风");
                } else {
                    menuText.add("开启麦克风");
                }

                if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                    if (haveDownMai) {
                        haveDownMai = false;
                    }
                } else {
                    menuText.add("下麦");
                }
                showLianmaiMenu(menuText);
            } else {
                if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_NAMED) {
                    cancelLianmai("取消举手");
                } else {
                    cancelLianmai("取消排麦");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连麦菜单
     *
     * @param menuText
     */
    private void showLianmaiMenu(List<String> menuText) {
        BottomMenuDialog lianmaiDialog = new BottomMenuDialog(this, menuText, true);
        lianmaiDialog.setOnMenuItemClickListener(new BottomMenuDialog.OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                if (index == 0) {
                    if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo()) {
                        mCCAtlasClient.switchCamera();
                    } else {
                        Tools.showToast("不支持当前操作，摄像头被关闭了");
                    }
                } else if (index == 1) {
                    if (mCCAtlasClient.getMediaMode() == MEDIA_MODE_BOTH) {
                        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo()) {
                            mCCAtlasClient.disableVideo(true);
                        } else {
                            mCCAtlasClient.enableVideo(true);
                        }
                    } else {
                        Tools.showToast("老师已经设置当前直播间的连麦模式为仅音频");
                    }
                } else if (index == 2) {
                    CCInteractBean ccInteractBean = mCCAtlasClient.getInteractBean();
                    boolean allowAudio = ccInteractBean != null && ccInteractBean.getUserSetting().isAllowAudio();
                    if (allowAudio) {
//                        mCCAtlasClient.disableAudio(true);
                        //不需要而额外控制权限的可以直接走上面的代码，后台判断是否可以修改用户状态
                        mCCAtlasClient.mediaSwitchAudioUserid(false, ccInteractBean.getUserId(), TALKER, new CCAtlasCallBack<String>() {
                            @Override
                            public void onSuccess(String object) {
                                mCCAtlasClient.disableAudio(true);
                            }

                            @Override
                            public void onFailure(int errCode, String errMsg) {
                                Tools.showToast(errMsg);
                            }
                        });
                    } else {
                        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
//                            mCCAtlasClient.enableAudio(true);
                            //不需要而额外控制权限的可以直接走上面的代码，后台判断是否可以修改用户状态
                            mCCAtlasClient.mediaSwitchAudioUserid(true, ccInteractBean.getUserId(), TALKER, new CCAtlasCallBack<String>() {
                                @Override
                                public void onSuccess(String object) {
                                    mCCAtlasClient.enableAudio(true);
                                }

                                @Override
                                public void onFailure(int errCode, String errMsg) {
                                    Tools.showToast(errMsg);
                                }
                            });
                        } else {
                            Tools.showToast("老师已经设置当前直播间的麦克风关闭");
                        }
                    }
                } else {
                    handDownMai();
                }
            }
        });
    }

    private void handDownMai() {
        showLoading();
        mCCBarLeyManager.handsDown(new CCBarLeyCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoading();
            }

            @Override
            public void onFailure(String err) {
                dismissLoading();
                Tools.showToast(err);
            }
        });
    }

    /**
     * 取消连麦/举手
     *
     * @param tip
     */
    private void cancelLianmai(String tip) {
        final CustomTextViewDialog dialog = new CustomTextViewDialog(this);
        dialog.setMessage(tip);
        dialog.setBtn("取消", "确认", new CustomTextViewDialog.CustomClickListener() {
            @Override
            public void onLeftClickListener() {

            }

            @Override
            public void onRightClickListener() {
                cancelQueueMai();
            }

            @Override
            public void onOneBtnClickListener() {

            }
        });
    }

    /**
     * 取消连麦/举手
     */
    private void cancelQueueMai() {
        showLoading();
        mCCBarLeyManager.handsUpCancel(new CCBarLeyCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoading();
                updateMaiButton(MAI_STATUS_NORMAL);
            }

            @Override
            public void onFailure(String err) {
                dismissLoading();
                Tools.showToast(err);
            }
        });
    }

    /**
     * 更新排麦按钮
     *
     * @param status
     */
    private void updateMaiButton(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMaiStatus = status;
                menuBottomView.setLianmaiBtnVisibility(View.VISIBLE);
                menuBottomView.setHandupBtnVisibility(View.GONE);
                switch (status) {
                    case MAI_STATUS_NORMAL:
                        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_FREE) {
                            menuBottomView.setLianmaiText("");
                            menuBottomView.setLianmaiStatus(0);
                        } else if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                            menuBottomView.setLianmaiBtnVisibility(View.GONE);
                            menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                        } else {
                            menuBottomView.setLianmaiText("");
                            menuBottomView.setLianmaiStatus(3);
                        }
                        break;
                    case MAI_STATUS_QUEUE://排队中
                        if (mCCAtlasClient.getInteractBean() != null && (mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_FREE
                                || mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO)) {
                            if (mQueueIndex == -1) {
                                menuBottomView.setLianmaiText("    排麦中");
                            } else {
                                SpannableString maiStr = new SpannableString("    排麦中\n    第" + mQueueIndex + "位");
                                menuBottomView.setLianmaiText(maiStr.toString());
                            }
                            menuBottomView.setLianmaiStatus(1);
                        } else if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                            menuBottomView.setLianmaiStatus(4);
                            menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                        } else {
                            menuBottomView.setLianmaiStatus(4);
                        }
                        break;
                    case MAI_STATUS_ING://连接成功
                        Tools.showToast("连麦成功");

                        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getLianmaiMode() == LIANMAI_MODE_AUTO) {
                            menuBottomView.setHandupBtnVisibility(View.VISIBLE);
                        }

                        menuBottomView.setLianmaiText("");
                        menuBottomView.setLianmaiStatus(2);
                        break;
                }
            }
        });
    }

    /**
     * 权限回调
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void doRequestMai() {
        startLianmai();
    }

    /**
     * 开始连麦
     */
    private void startLianmai() {
        mCCBarLeyManager.handsup(new CCBarLeyCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mQueueIndex = -1;
                updateMaiButton(MAI_STATUS_QUEUE);
            }

            @Override
            public void onFailure(String err) {
                Tools.showToast(err);
            }
        });
    }

    /**
     * 显示点名互动视图
     */
    protected void showCallNamed(int time) {
        closeAllDialog();
        mRollCallDialog = new CallNameDialog(this, time, new CallNameDialog.OnCallNameClickListener() {
            @Override
            public void onAnswer() {
                mCCAtlasClient.studentNamed();
            }
        });
    }

    protected VoteDialog voteDialog;
    private ArrayList<Integer> voteResults;

    /**
     * 显示投票互动视图
     *
     * @param vote
     */
    protected void showVote(final Vote vote) {
        closeAllDialog();
        voteDialog = new VoteDialog(this, vote, new VoteDialog.OnVoteClickListener() {
            @Override
            public void onCommit(boolean isSingle, ArrayList<Integer> mResults) {
                voteResults = mResults;
                mCCAtlasClient.sendVoteSelected(vote.getVoteId(), vote.getPublisherId(), isSingle, mResults);
            }
        });
    }

    /**
     * 停止答题
     */
    private void dismissVote() {
        if (voteDialog != null && voteDialog.isShowing()) {
            voteDialog.dismiss();
            voteDialog = null;
            if (voteResults != null)
                voteResults.clear();
            voteResults = null;
        }
    }

    private VoteResultDialog voteResultDialog;

    /**
     * 显示投票结果
     *
     * @param voteResult
     */
    protected void showVoteResult(VoteResult voteResult) {
        closeAllDialog();
        voteResultDialog = new VoteResultDialog(this, voteResult, voteResults);
    }

    /**
     * 关闭投票结果
     */
    private void dismissVoteResult() {
        if (voteResultDialog != null && voteResultDialog.isShowing()) {
            voteResultDialog.dismiss();
            voteResultDialog = null;
        }
    }

    /**
     * 头脑风暴弹窗
     */
    protected BrainStomDialog brainStomDialog;

    /**
     * 显示头脑风暴
     *
     * @param brainStom
     */
    protected void showBrainStom(final BrainStom brainStom) {
        closeAllDialog();
        brainStomDialog = new BrainStomDialog(this, brainStom, new BrainStomDialog.OnCommitClickListener() {
            @Override
            public void onCommit(String content) {
                try {
                    mCCAtlasClient.sendBrainStomData(brainStom.getBrainStomID(), content, brainStom.getTileName());
                } catch (Exception e) {
                    Tools.showToast(e.getMessage());
                }
            }
        });
    }

    /**
     * 关闭头脑风暴
     */
    private void dismissBrainStom() {
        if (brainStomDialog != null && brainStomDialog.isShowing()) {
            brainStomDialog.dismiss();
        }
    }

    /**
     * 投票
     */
    protected BallotDialog ballotDialog;

    /**
     * 显示投票
     *
     * @param mBallot
     */
    protected void showBallot(final Ballot mBallot) {
        closeAllDialog();
        ballotDialog = new BallotDialog(this, mBallot, new BallotDialog.OnCommitClickListener() {
            @Override
            public void onCommit() {
                try {
                    mCCAtlasClient.sendBallotData(mBallot.getBallotId(), Config.mBallotResults, mBallot.getTileName());
                } catch (Exception e) {
                    Tools.showToast(e.getMessage());
                }
            }
        });
    }

    /**
     * 关闭投票
     */
    private void dismissBallot() {
        if (ballotDialog != null && ballotDialog.isShowing()) {
            ballotDialog.dismiss();
            ballotDialog = null;
        }
    }

    private BallotResultDialog ballotResultDialog;

    /**
     * 显示投票结果
     *
     * @param ballotResult
     */
    private void showBallotResult(BallotResult ballotResult) {
        closeAllDialog();
        dismissBallotResult();
        ballotResultDialog = new BallotResultDialog(this, ballotResult);
    }

    /**
     * 关闭投票结果
     */
    private void dismissBallotResult() {
        if (ballotResultDialog != null && ballotResultDialog.isShowing()) {
            ballotResultDialog.dismiss();
            ballotResultDialog = null;
        }
    }

    /**
     * 关闭所有对话框
     */
    private void closeAllDialog() {
        dismissVote();
        dismissVoteResult();
        dismissBrainStom();
        dismissBallot();
        dismissBallotResult();
    }


    /**
     * 系统界面的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.LECTURE_REQUEST_CODE) {
            if (ccDocView != null) {
                ccDocView.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (ccDocView != null && ccDocView.isDocFullScreen()) {
            ccDocView.docExitFullScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (interCutVideoView != null) {
            interCutVideoView.onResume();
        }

        if (warmUpVideoView != null) {
            warmUpVideoView.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (interCutVideoView != null) {
            interCutVideoView.onStop();
        }

        if (warmUpVideoView != null) {
            warmUpVideoView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 关闭流
     */
    @Override
    protected void exitRoom() {
        showLoading();
        if (mMaiStatus == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
            mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    releaseViewData();
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    dismissLoading();
                    Tools.showToast(errMsg);
                }
            });
        } else {
            mCCAtlasClient.leave(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    releaseViewData();
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    dismissLoading();
                    Tools.showToast(errMsg);
                }
            });
        }
    }

    /**
     * 释放视图资源
     */
    @Override
    protected void releaseViewData() {
        isExit = true;

        //暖场视频
        if (warmUpVideoView != null) {
            warmUpVideoView.closeVideo();
            warmUpVideoView = null;
        }
        //视频管理类
        if (mVideoManager != null) {
            mVideoManager.onClearDatas();
            mVideoManager = null;
        }
        if (ccDocView != null) {
            ccDocView.release();
        }

        finish();
    }

    /**
     * 主视频平铺等处理菜单点击
     */
    @Override
    public void toggleTopAndBottom() {
        if (menuTopView.isPerformingAnim()) {
            return;
        }
        int currentType = mCCAtlasClient.getInteractBean().getTemplate();
        boolean bottomAnim = currentType != CCAtlasClient.TEMPLATE_SPEAK || sClassDirection == 1;
        if (menuTopView.isShow()) {
            menuTopView.startHideAnim();
            if (bottomAnim) {
                menuBottomView.startHideAnim();
                chatView.setVisibility(View.GONE);
            }

        } else {
            menuTopView.startShowAnim();
            if (bottomAnim) {
                menuBottomView.startShowAnim();
                chatView.setVisibility(View.VISIBLE);
            }

        }
    }

    private OnUserCupCountListener onUserCupCountListener;

    private void setOnUserCupCountListener(OnUserCupCountListener onUserCupCountListener) {
        this.onUserCupCountListener = onUserCupCountListener;
    }

    interface OnUserCupCountListener {
        void getUserCupCount(ArrayList<CCUser> users);
    }

    public void upDataCup() {
        mCCAtlasClient.getLiveStatus(new CCAtlasCallBack<CCStartBean>() {
            @Override
            public void onSuccess(CCStartBean ccStartBean) {
                mCCAtlasClient.getRoomReward(ccStartBean.getLiveId(), mCCAtlasClient.getInteractBean().getRoom().getRoomId(), new CCAtlasCallBack<String>() {
                    @Override
                    public void onSuccess(String json) {
                        if (json == null) {
                            return;
                        }
                        try {
                            JSONObject data = new JSONObject(json);
                            JSONObject jsonObject = data.getJSONObject("data");
                            JSONObject total_cup = jsonObject.getJSONObject("total_cup");
                            Iterator<String> cup_Iterator = total_cup.keys();
                            final ArrayList<CCUser> users = new ArrayList<>();
                            while (cup_Iterator.hasNext()) {
                                // 获得key
                                String cupKey = cup_Iterator.next();
                                CCUser ccUser = new CCUser();
                                ccUser.setUserId(cupKey);
                                ccUser.setCupIndex(total_cup.getInt(cupKey));
                                ccUser.setSendCup(true);
                                users.add(ccUser);
                            }
                            if (onUserCupCountListener != null) {
                                onUserCupCountListener.getUserCupCount(users);
                            }
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {

                    }
                });
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        });
    }
}
