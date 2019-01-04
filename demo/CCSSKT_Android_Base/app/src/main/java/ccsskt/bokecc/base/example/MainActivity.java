package ccsskt.bokecc.base.example;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.sskt.base.CCAtlasCallBack;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.CCInteractSDK;
import com.bokecc.sskt.base.CCStream;
import com.bokecc.sskt.base.LocalStreamConfig;
import com.bokecc.sskt.base.MyBroadcastReceiver;
import com.bokecc.sskt.base.SubscribeRemoteStream;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.CCUserRoomStatus;
import com.bokecc.sskt.base.bean.ChatMsg;
import com.bokecc.sskt.base.exception.StreamException;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.bokecc.sskt.base.view.CCMediaSurfaceView;
import com.example.ccbarleylibrary.CCBarLeyManager;
import com.example.ccbarleylibrary.CCBarLeyCallBack;
import com.example.ccchatlibrary.CCChatManager;
import com.intel.webrtc.base.LocalCameraStreamParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;

import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.OnClick;
import ccsskt.bokecc.base.example.base.BaseActivity;
import ccsskt.bokecc.base.example.util.DensityUtil;

/**
 * 作者 ${CC视频}.<br/>
 */

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_USER_ACCOUNT = "user_account";
    private static final String KEY_ROOM_ID = "room_id";

    private static Intent newIntent(Context context, String sessionid, String roomid, String userAccount) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_SESSION_ID, sessionid);
        intent.putExtra(KEY_USER_ACCOUNT, userAccount);
        intent.putExtra(KEY_ROOM_ID, roomid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static void startSelf(Context context, String sessionid, String roomid, String userAccount) {
        context.startActivity(newIntent(context, sessionid, roomid, userAccount));
    }

    @BindView(R.id.id_root_drawer)
    DrawerLayout mRootDrawer;
    @BindView(R.id.id_local_container)
    LinearLayout mLocalContainer;
    @BindView(R.id.id_remote_mix_container)
    LinearLayout mRemoteMixContainer;
    @BindView(R.id.id_preview)
    Button mPreviewBtn;
    @BindView(R.id.id_disable_local_video)
    Button mDisableLocalVideoBtn;
    @BindView(R.id.id_disable_local_audio)
    Button mDisableLocalAudioBtn;
    @BindView(R.id.id_disable_remote_video)
    Button mDisableRemoteVideoBtn;
    @BindView(R.id.id_disable_remote_audio)
    Button mDisableRemoteAudioBtn;
    @BindView(R.id.id_pause_remote_audio)
    Button mPauseRemoteAudioBtn;
    @BindView(R.id.id_pause_remote_video)
    Button mPauseRemoteVideoBtn;
    @BindView(R.id.id_publish)
    Button mPublishBtn;
    @BindView(R.id.id_subscribe)
    Button mSubscribeBtn;
    @BindView(R.id.id_rtmp)
    Button mRtmpBtn;
    @BindView(R.id.id_white_board)
    Button mWhiteBtn;
    @BindView(R.id.id_chat)
    Button mChatBtn;
    @BindView(R.id.id_pic)
    ImageView mPic;

    @BindView(R.id.id_class_madie_video_container)
    FrameLayout mMadieVideoContainer;
    @BindView(R.id.id_media_surface)
    CCMediaSurfaceView idCCMediaSurface;

    private CCSurfaceRenderer mLocalRenderer, mRemoteMixRenderer;
    private CCStream mLocalStream, mStream;


    private CCDocViewManager mDocViewManager;
    private CCBarLeyManager mBarLeyManager;
    private CCChatManager mCCChatManager;
    private CCAtlasClient.OnNotifyStreamListener mClientObserver = new CCAtlasClient.OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(SubscribeRemoteStream stream) {
//            if (stream.getRemoteStream().isRemoteIsLocal()) { // 不订阅自己的本地流
//                return;
//            }
            Log.e(TAG, "onStreamAdded: [ " + stream.getStreamId() + " ]");
            if (stream.getRemoteStream().getStreamType() != CCStream.REMOTE_MIX) {
                // 订阅
                mStream = stream.getRemoteStream();
            }
        }

        @Override
        public void onStreamRemoved(SubscribeRemoteStream stream) {
            Log.e(TAG, "onStreamRemoved: [ " + stream.getRemoteStream().getStreamId() + " ]");
            if (stream.getRemoteStream().getStreamType() != CCStream.REMOTE_MIX) {
                mStream = null;
                try {
                    ccAtlasClient.unSubscribeStream(stream.getRemoteStream(), null);
                } catch (StreamException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStreamError() {

        }
    };

    private CCChatManager.OnChatListener mChatList = new CCChatManager.OnChatListener() {
        @Override
        public void onReceived(CCUser from, ChatMsg msg, boolean self) {
            Log.i(TAG, "onReceived: " + msg.getMsg() + msg.getTime());
        }

        @Override
        public void onError(String err) {

        }
    };

    private boolean isFront;
    private boolean isPublish = false;

    private String rtmp;
    private DrawHandler mDrawHandler;
    int vWidth = 0;
    int vHeight = 0;
    private int mMadieScreenLeft = 0, mMadieScreenTop = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void beforeSetContentView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onViewCreated() {

        mDisableLocalVideoBtn.setSelected(false);
        mDisableLocalAudioBtn.setSelected(false);
        mDisableRemoteAudioBtn.setSelected(false);
        mDisableRemoteVideoBtn.setSelected(false);
        mPauseRemoteAudioBtn.setSelected(false);
        mPauseRemoteVideoBtn.setSelected(false);
        mPublishBtn.setSelected(false);
        mSubscribeBtn.setSelected(false);
        mRtmpBtn.setSelected(false);

        ccAtlasClient.setOnNotifyStreamListener(mClientObserver);
        mDocViewManager = CCDocViewManager.getInstance();
//        mBarLeyManager = BarLeyManager.getInstance();
//        mBarLeyManager.setOnNotifyStreamListener(mClientObserver);
        mCCChatManager = CCChatManager.getInstance();
        mCCChatManager.setOnChatListener(mChatList);

        initRenderer();
        mDrawHandler = new DrawHandler(Looper.getMainLooper());

        mRootDrawer.addDrawerListener(this);

        final String sessionid = getIntent().getStringExtra(KEY_SESSION_ID);
        final String userAccount = getIntent().getStringExtra(KEY_USER_ACCOUNT);
        rtmp = "rtmp://push-cc1.csslcloud.net/origin/" + getIntent().getStringExtra(KEY_ROOM_ID);
//        doMadieScreenLayoutTouch();
//        ccAtlasClient.dispatch(getIntent().getStringExtra(KEY_USER_ACCOUNT), null);
        createLocalStream();
        initMedia();

        //用户自己定义的socket事件
        ccAtlasClient.setOnPublishMessageListener(mPublishMessage);
        //人员加入房间/退出房间通知事件
        ccAtlasClient.setOnUserRoomStatus(mUpdateUserList);
        //人员在举手连麦模式下，举手通知事件
        ccAtlasClient.setOnUserHand(mUserHand);
//        Toast.makeText(this,  "您被老师授权标注", Toast.LENGTH_SHORT).show();

        idCCMediaSurface.setOnVideoWHListener(new CCMediaSurfaceView.OnVideoWHListener() {
            @Override
            public void setVideoWH(int w, int h) {
                vWidth = w;
                vHeight = h;
                changeVideo(mMadieVideoContainer, DensityUtil.dp2px(MainActivity.this, 160),
                        idCCMediaSurface);
            }
        });

        idCCMediaSurface.setOnIsVisiableMadieListener(new CCMediaSurfaceView.OnIsVisiableMadieListener() {
            @Override
            public void isShowMadie(final boolean isShow) {

                showView(mMadieVideoContainer, isShow);
            }
        });


    }

    private void initMedia(){
        String url = ccAtlasClient.getString(CCAtlasClient.MEDIA_URL);
        //开启直播的时候判断是否有插播音视频
        if (!TextUtils.isEmpty(url)) {
            mMadieVideoContainer.setVisibility(View.VISIBLE);
        } else {
            mMadieVideoContainer.setVisibility(View.GONE);
        }
        //是不是要隐藏播放视频
        showView(mMadieVideoContainer, idCCMediaSurface.getVideoType());
    }

    private void showView(final View view, final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    mMadieVideoContainer.setVisibility(View.VISIBLE);
                } else {
                    mMadieVideoContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void changeVideo(FrameLayout relativeLayout, int width, View surfaceView) {
        // 该LinearLayout的父容器 android:orientation="vertical" 必须
        FrameLayout linearLayout = (FrameLayout) relativeLayout;
        int lw = width;
        int lh = linearLayout.getHeight();
        float max = 1;

        // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
//        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            //竖屏模式下按视频宽度计算放大倍数值
//            max = (float) vWidth / (float) lw;
//        }
        if(vWidth<vHeight){
            max = (float) vHeight / (float) lw;
        }else {
            max = (float) vWidth / (float) lw;
        }
        // 选择大的一个进行缩放

        int vWidthTemp = (int) Math.ceil((float) vWidth / max);
        int vHeightTemp = (int) Math.ceil((float) vHeight / max);

        // 设置surfaceView的布局参数
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vWidthTemp, vHeightTemp);
        lp.gravity = (Gravity.CENTER);
        surfaceView.setLayoutParams(lp);
    }

    private void doMadieScreenLayoutTouch() {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMadieVideoContainer.getLayoutParams();
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mMadieScreenLeft = params.leftMargin;
                mMadieScreenTop = params.topMargin;
                RelativeLayout.LayoutParams temp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                temp.leftMargin = 0;
                temp.topMargin = 0;
                mMadieVideoContainer.setLayoutParams(temp);
//                FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                videoParams.gravity = Gravity.CENTER;
//                idCCMediaSurface.setLayoutParams(videoParams);

                changeVideo(mMadieVideoContainer, DensityUtil.getWidth(MainActivity.this), idCCMediaSurface);

                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //计算移动的距离
                int offX = (int) (e2.getX() - e1.getX());
                int offY = (int) (e2.getY() - e1.getY());
                params.topMargin = params.topMargin + offY;
                params.leftMargin = params.leftMargin + offX;
                if (params.topMargin < 0) {
                    params.topMargin = 0;
                }
                if (params.leftMargin < 0) {
                    params.leftMargin = 0;
                }
                if (params.topMargin > (DensityUtil.getHeight(MainActivity.this) - mMadieVideoContainer.getHeight()
                        - DensityUtil.dp2px(MainActivity.this, 30))) {
                    params.topMargin = DensityUtil.getHeight(MainActivity.this) - mMadieVideoContainer.getHeight() - DensityUtil.dp2px(MainActivity.this, 30);
                }
                if (params.leftMargin > (DensityUtil.getWidth(MainActivity.this) - mMadieVideoContainer.getWidth())) {
                    params.leftMargin = DensityUtil.getWidth(MainActivity.this) - mMadieVideoContainer.getWidth();
                }
                mMadieVideoContainer.setLayoutParams(params);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        mMadieVideoContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean temp = (gestureDetector.onTouchEvent(event));
                return temp;
            }
        });
    }

    //人员在举手连麦模式下，举手通知事件
    private CCAtlasClient.OnUserHand mUserHand = new CCAtlasClient.OnUserHand() {
        @Override
        public void UserHand(CCUser user) {
            pusher(user.getUserName());
        }
    };

    //人员加入房间/退出房间通知事件
    private CCAtlasClient.OnUserRoomStatus mUpdateUserList = new CCAtlasClient.OnUserRoomStatus() {
        @Override
        public void OnExitRoomUser(CCUserRoomStatus ccUserRoomStatus) {
            message(ccUserRoomStatus.getUserName() + "退出房间");
        }

        @Override
        public void OnJoinRoomUser(CCUserRoomStatus ccUserRoomStatus) {
            message(ccUserRoomStatus.getUserName() + "加入房间");
        }
    };

    //用户监听自己设置的socket事件
    private CCAtlasClient.OnPublishMessageListener mPublishMessage = new CCAtlasClient.OnPublishMessageListener() {
        @Override
        public void onPublishMessage(JSONObject object) {
            try {
                message(object.getString("action"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void message(final String string){
        Message msg = new Message();
        msg.obj = string;
        msg.what = 1;
        mDrawHandler.sendMessage(msg);

    }

    private final class DrawHandler extends android.os.Handler {

        DrawHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
           pusher(msg.obj.toString());
        }
    }

    private void pusher(String str){
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void initRenderer() {
        mLocalRenderer = new CCSurfaceRenderer(this);
        mLocalRenderer.init(ccAtlasClient.getEglBase().getEglBaseContext(), null);
        mLocalRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mLocalContainer.addView(mLocalRenderer);

        mRemoteMixRenderer = new CCSurfaceRenderer(this);
        mRemoteMixRenderer.init(ccAtlasClient.getEglBase().getEglBaseContext(), null);
        mRemoteMixRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mRemoteMixContainer.addView(mRemoteMixRenderer);
    }

    private void createLocalStream() {
        try {
            ccAtlasClient.setCameraType(LocalCameraStreamParameters.CameraType.FRONT);
            ccAtlasClient.createLocalStream(ccAtlasClient.getMediaMode());
        } catch (StreamException e) {
            showToast(e.getMessage());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (idCCMediaSurface != null) {
            idCCMediaSurface.seekToVideo();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (idCCMediaSurface != null) {
            idCCMediaSurface.pauseVideo();
        }
    }
    @Override
    protected void onDestroy() {
        if (mLocalRenderer != null) {
            mLocalRenderer.cleanFrame();
            mLocalRenderer.release();
        }
        if (mRemoteMixRenderer != null) {
            mRemoteMixRenderer.cleanFrame();
            mRemoteMixRenderer.release();
        }
        if (mStream != null) {
            mStream.detach();
            mStream = null;
        }
        if (mLocalStream != null) {
            mLocalStream.detach();
            ccAtlasClient.destoryLocalStream();
        }
        MyBroadcastReceiver.getInstance().unMyBroadcastReceiver();
        ccAtlasClient = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showProgress();
        ccAtlasClient.leave(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissProgress();
                ccAtlasClient.CCReportLogInfo();
                finish();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissProgress();
                showToast(errMsg);
            }
        });
    }

    @OnClick(R.id.id_preview)
    void preview() {
        try {
            ccAtlasClient.attachLocalCameraStram(mLocalRenderer);
            mPreviewBtn.setEnabled(false);
        } catch (StreamException ignored) {
        }
    }

    @OnClick(R.id.id_start)
    void start() {
        ccAtlasClient.startLive(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("start live success");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast("start live failed [ " + errMsg + " ]");
            }
        });
    }

    @OnClick(R.id.id_stop)
    void stop() {
        ccAtlasClient.stopLive(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("stop live success");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast("stop live failed [ " + errMsg + " ]");
            }
        });
    }

    @OnClick(R.id.id_switch)
    void switchCamera() {
        ccAtlasClient.switchCamera(new CCAtlasCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                isFront = aBoolean;
                mLocalRenderer.setMirror(isFront);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        });
    }

    @OnClick(R.id.id_publish)
    void publish() {
        if (mPublishBtn.isSelected()) {
            showProgress();
            ccAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    isPublish = false;
                    dismissProgress();
                    showToast("unpublish success");
                    mPublishBtn.setSelected(!mPublishBtn.isSelected());
                    mPublishBtn.setText(mPublishBtn.isSelected() ? "停止发布" : "发布");
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    dismissProgress();
                    showToast("unpublish failed [ " + errMsg + " ]");
                }
            });
        } else {
            showProgress();
            ccAtlasClient.publish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    isPublish = true;
                    dismissProgress();
                    showToast("publish success");
                    mPublishBtn.setSelected(!mPublishBtn.isSelected());
                    mPublishBtn.setText(mPublishBtn.isSelected() ? "停止发布" : "发布");
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    dismissProgress();
                    showToast("publish failed [ " + errMsg + " ]");
                }
            });
        }
    }

    @OnClick(R.id.id_subscribe)
    void subscribe() {
        if (mStream != null) {
            if (mSubscribeBtn.isSelected()) {
                showProgress();
                try {
                    ccAtlasClient.unSubscribeStream(mStream, new CCAtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissProgress();
                            showToast("unsubscribe success");
                            mSubscribeBtn.setSelected(!mSubscribeBtn.isSelected());
                            mSubscribeBtn.setText(mSubscribeBtn.isSelected() ? "取消订阅" : "订阅");
                            try {
                                mStream.detach(mRemoteMixRenderer);
                            } catch (StreamException ignored) {
                            } finally {
                                mRemoteMixRenderer.cleanFrame();
                            }
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissProgress();
                            showToast("unsubscribe failed [ " + errMsg + " ]");
                        }
                    });
                } catch (StreamException e) {
                    e.printStackTrace();
                }
            } else {
                showProgress();
                try {
                    ccAtlasClient.SubscribeStream(mStream, new CCAtlasCallBack<CCStream>() {
                        @Override
                        public void onSuccess(CCStream stream) {
                            dismissProgress();
                            showToast("subscribe success");
                            mSubscribeBtn.setSelected(!mSubscribeBtn.isSelected());
                            mSubscribeBtn.setText(mSubscribeBtn.isSelected() ? "取消订阅" : "订阅");
                            try {
                                mStream.attach(mRemoteMixRenderer);
                            } catch (StreamException ignored) {
                            }
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissProgress();
                            showToast("subscribe failed [ " + errMsg + " ]");
                            ccAtlasClient.leave(new CCAtlasCallBack<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //                        ccAtlasClient.join();
                                    ccAtlasClient.getToken(new CCAtlasCallBack<String>() {
                                        @Override
                                        public void onSuccess(String str) {

                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    });
                } catch (StreamException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.id_disable_local_audio)
    void disableLocalAduio() {
        if (mLocalStream != null) {
            if (mDisableLocalAudioBtn.isSelected()) {
                mLocalStream.enableAudio();
            } else {
                mLocalStream.disableAudio();
            }
            mDisableLocalAudioBtn.setSelected(!mDisableLocalAudioBtn.isSelected());
            mDisableLocalAudioBtn.setText(mDisableLocalAudioBtn.isSelected() ? "开启本地音频" : "关闭本地音频");
        }
    }

    @OnClick(R.id.id_disable_local_video)
    void disableLocalVideo() {
        if (mLocalStream != null) {
            if (mDisableLocalVideoBtn.isSelected()) {
                mLocalStream.enableVideo();
            } else {
                mLocalStream.disableVideo();
            }
            mDisableLocalVideoBtn.setSelected(!mDisableLocalVideoBtn.isSelected());
            mDisableLocalVideoBtn.setText(mDisableLocalVideoBtn.isSelected() ? "开启本地视频" : "关闭本地视频");
        }
    }

    @OnClick(R.id.id_disable_remote_audio)
    void disableRemoteAudio() {
        if (mStream != null) {
            if (mDisableRemoteAudioBtn.isSelected()) {
                mStream.enableAudio();
            } else {
                mStream.disableAudio();
            }
            mDisableRemoteAudioBtn.setSelected(!mDisableRemoteAudioBtn.isSelected());
            mDisableRemoteAudioBtn.setText(mDisableRemoteAudioBtn.isSelected() ? "开启远程音频" : "关闭远程音频");
        }
    }

    @OnClick(R.id.id_disable_remote_video)
    void disableRemoteVideo() {
        if (mStream != null) {
            if (mDisableRemoteVideoBtn.isSelected()) {
                mStream.enableVideo();
            } else {
                mStream.disableVideo();
            }
            mDisableRemoteVideoBtn.setSelected(!mDisableRemoteVideoBtn.isSelected());
            mDisableRemoteVideoBtn.setText(mDisableRemoteVideoBtn.isSelected() ? "开启远程视频" : "关闭远程视频");
        }
    }

    @OnClick(R.id.id_pause_remote_audio)
    void pauseRemoteAudio() {
        if (mStream != null) {
            if (mPauseRemoteAudioBtn.isSelected()) {
                ccAtlasClient.playAudio(mStream, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("play audio success");
                        mPauseRemoteAudioBtn.setSelected(!mPauseRemoteAudioBtn.isSelected());
                        mPauseRemoteAudioBtn.setText(mPauseRemoteAudioBtn.isSelected() ? "恢复拉远程音频" : "暂停拉远程音频");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("play audio failed [ " + errMsg + " ]");
                    }
                });
            } else {
                ccAtlasClient.pauseAudio(mStream, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("pause audio success");
                        mPauseRemoteAudioBtn.setSelected(!mPauseRemoteAudioBtn.isSelected());
                        mPauseRemoteAudioBtn.setText(mPauseRemoteAudioBtn.isSelected() ? "恢复拉远程音频" : "暂停拉远程音频");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("pause audio failed [ " + errMsg + " ]");
                    }
                });
            }
        }
    }

    @OnClick(R.id.id_pause_remote_video)
    void pauseRemoteVideo() {
        if (mStream != null) {
            if (mPauseRemoteVideoBtn.isSelected()) {
                ccAtlasClient.playVideo(mStream, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("play video success");
                        mPauseRemoteVideoBtn.setSelected(!mPauseRemoteVideoBtn.isSelected());
                        mPauseRemoteVideoBtn.setText(mPauseRemoteVideoBtn.isSelected() ? "恢复拉远程视频" : "暂停拉远程视频");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("play video failed [ " + errMsg + " ]");
                    }
                });
            } else {
                ccAtlasClient.pauseVideo(mStream, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("pause video success");
                        mPauseRemoteVideoBtn.setSelected(!mPauseRemoteVideoBtn.isSelected());
                        mPauseRemoteVideoBtn.setText(mPauseRemoteVideoBtn.isSelected() ? "恢复拉远程视频" : "暂停拉远程视频");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("pause video failed [ " + errMsg + " ]");
                    }
                });
            }
        }
    }

    @OnClick(R.id.id_rtmp)
    void pushRtmp() {
        if (isPublish) {
            if (mRtmpBtn.isSelected()) {
                ccAtlasClient.removeExternalOutput(rtmp, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("移除rtmp推流成功");
                        mRtmpBtn.setSelected(!mRtmpBtn.isSelected());
                        mRtmpBtn.setText(mRtmpBtn.isSelected() ? "移除rtmp推流" : "添加rtmp推流");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("移除rtmp推流失败");
                    }
                });
            } else {
                ccAtlasClient.addExternalOutput(rtmp, new CCAtlasCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("添加rtmp推流成功");
                        mRtmpBtn.setSelected(!mRtmpBtn.isSelected());
                        mRtmpBtn.setText(mRtmpBtn.isSelected() ? "移除rtmp推流" : "添加rtmp推流");
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        showToast("添加rtmp推流失败");
                    }
                });
            }
        }
    }

    @OnClick(R.id.id_take_pic)
    void takePic() {
        mLocalRenderer.getBitmap(new CCSurfaceRenderer.OnShotCallback() {
            @Override
            public void onShot(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPic.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    @OnClick(R.id.id_white_board)
    void goWhiteBoard() {
        go(WhiteBoardActivity.class);
    }

    @OnClick(R.id.id_chat)
    void goChat() {
        go(ChatActivity.class);
    }

    @OnClick(R.id.id_publishmessage)
    void goPublish() {
        JSONObject data = new JSONObject();
        try {
            data.put("action","nihao");
            //用户自己定义socket事件
            ccAtlasClient.sendPublishMessage(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // 解决和surfaceview遮挡问题
        mRootDrawer.bringChildToFront(drawerView);
        mRootDrawer.requestLayout();
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
