package ccsskt.bokecc.base.example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.sskt.base.CCAtlasCallBack;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.CCStream;
import com.bokecc.sskt.base.LocalStreamConfig;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.ChatMsg;
import com.bokecc.sskt.base.exception.StreamException;
import com.bokecc.sskt.base.renderer.CCSurfaceRenderer;
import com.example.ccbarleylibrary.CCBarLeyManager;
import com.example.ccbarleylibrary.CCBarLeyCallBack;
import com.example.ccchatlibrary.CCChatManager;

import org.webrtc.RendererCommon;

import butterknife.BindView;
import butterknife.OnClick;
import ccsskt.bokecc.base.example.base.BaseActivity;

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

    private CCSurfaceRenderer mLocalRenderer, mRemoteMixRenderer;
    private CCStream mLocalStream, mStream;


    private CCDocViewManager mDocViewManager;
    private CCBarLeyManager mBarLeyManager;
    private CCChatManager mCCChatManager;
    private CCAtlasClient.OnNotifyStreamListener mClientObserver = new CCAtlasClient.OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(CCStream stream) {
            if (stream.isRemoteIsLocal()) { // 不订阅自己的本地流
                return;
            }
            Log.e(TAG, "onStreamAdded: [ " + stream.getStreamId() + " ]");
            if (stream.getStreamType() != CCStream.REMOTE_MIX) {
                // 订阅
                mStream = stream;
            }
        }

        @Override
        public void onStreamRemoved(CCStream stream) {
            Log.e(TAG, "onStreamRemoved: [ " + stream.getStreamId() + " ]");
            if (stream.getStreamType() != CCStream.REMOTE_MIX) {
                mStream = null;
                try {
                    ccAtlasClient.unSubscribeStream(stream, null);
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

//        ccAtlasClient.addAtlasObserver(mClientObserver);
        mDocViewManager = CCDocViewManager.getInstance();
//        mBarLeyManager = BarLeyManager.getInstance();
//        mBarLeyManager.setOnNotifyStreamListener(mClientObserver);
        mCCChatManager = CCChatManager.getInstance();
        mCCChatManager.setOnChatListener(mChatList);

        initRenderer();

        createLocalStream();

        mRootDrawer.addDrawerListener(this);

        final String sessionid = getIntent().getStringExtra(KEY_SESSION_ID);
        final String userAccount = getIntent().getStringExtra(KEY_USER_ACCOUNT);
        rtmp = "rtmp://push-cc1.csslcloud.net/origin/" + getIntent().getStringExtra(KEY_ROOM_ID);
        showProgress();
        ccAtlasClient.join(sessionid, userAccount, null, new CCAtlasCallBack<CCInteractBean>() {
            @Override
            public void onSuccess(CCInteractBean ccBaseBean) {
                dismissProgress();
                showToast("join room success");
//                ccAtlasClient.onRoomDisconnect();
//                ccAtlasClient.getToken(null);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissProgress();
                showToast(errMsg);
            }
        });
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
        LocalStreamConfig config = new LocalStreamConfig.LocalStreamConfigBuilder().build();
        isFront = config.cameraType == LocalStreamConfig.CAMERA_FRONT;
        try {
            mLocalStream = ccAtlasClient.createLocalStream(config);
        } catch (StreamException e) {
            showToast(e.getMessage());
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
        if (mLocalStream != null) {
            try {
                mLocalStream.attach(mLocalRenderer);
                mPreviewBtn.setEnabled(false);
            } catch (StreamException ignored) {
            }
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
