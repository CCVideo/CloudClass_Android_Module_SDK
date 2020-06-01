package com.bokecc.room.ui.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Toast;

import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.dialog.BottomMenuDialog;
import com.bokecc.common.dialog.CustomTextViewDialog;
import com.bokecc.common.utils.AndroidBug5497Workaround;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.view.video.listener.OnVideoClickListener;
import com.bokecc.room.ui.model.ChatEntity;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.doc.CCDocView;
import com.bokecc.room.ui.view.menu.MenuBottomTeacherView;
import com.bokecc.room.ui.view.menu.MenuMoreTeacherView;
import com.bokecc.room.ui.view.menu.MenuTopView;
import com.bokecc.room.ui.view.pop.BottomAssistantIconPopup;
import com.bokecc.room.ui.view.pop.BottomCancelPopup;
import com.bokecc.room.ui.view.pop.BottomIconPopup;
import com.bokecc.room.ui.view.video.BaseVideoManager;
import com.bokecc.room.ui.view.video.LectureVideoManager;
import com.bokecc.room.ui.view.video.MainVideoManager;
import com.bokecc.room.ui.view.video.TileVideoManager;
import com.bokecc.room.ui.view.video.listener.VideoViewListener;
import com.bokecc.room.ui.view.video.widget.InterCutVideoView;
import com.bokecc.room.ui.view.video.widget.SuspensionVideoView;
import com.bokecc.room.ui.view.widget.CupView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.SendReward;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.config.LogConfig;
import com.bokecc.sskt.base.common.util.CCStartBean;
import com.bokecc.ccdocview.model.DocInfo;
import com.bokecc.stream.bean.CCStream;
import com.example.ccbarleylibrary.CCBarLeyCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.bokecc.common.utils.Tools.showToast;
import static com.bokecc.sskt.base.CCAtlasClient.ASSISTANT;
import static com.bokecc.sskt.base.CCAtlasClient.PRESENTER;

@RuntimePermissions
public class TeacherRoomActivity extends CCRoomActivity implements OnVideoClickListener {

    private static final String TAG = "TeacherRoomActivity";
    /**
     * 顶部菜单
     */
    private MenuTopView menuTopView;
    /**
     * 底部菜单
     */
    private MenuBottomTeacherView menuBottomView;
    /**
     * 文档视图
     */
    private CCDocView ccDocView;
    private BottomMenuDialog cameraDialog;
    private CustomTextViewDialog stopLiveDialog;
    public static long mNamedTimeEnd = 0L;
    // 退出时候的操作
    private int mExitAction = -1;
    private static final int EXIT_ACTION_STOP = 0;
    private static final int EXIT_ACTION_CLOSE = 1;
    // 是否发布流
    private boolean isPublish = false;
    private VideoStreamView mSelfStreamView;
    private SurfaceView mSelfRenderer;
    protected CopyOnWriteArrayList<VideoStreamView> mVideoStreamViews = new CopyOnWriteArrayList<>();
    public static final String TEACHER_RESOLUTION = "teacher_resolution";
    /**
     * 视频管理类
     */
    private BaseVideoManager mVideoManager;
    /**
     * 开始直播弹框
     */
    private CustomTextViewDialog startLiveDialog;
    /**
     * 结束上一次直播Dialog
     */
    private CustomTextViewDialog endLastLiveDialog;
    /**
     * 手动录制Dialog
     */
    private CustomTextViewDialog recordDialog;

    /**
     * 更多菜单 view
     */
    private MenuMoreTeacherView menuMoreTeacherView;
    /**
     * 是否含有文档菜单
     */
    private boolean isMoreItemHasDoc;
    /**
     * 当前直播室模板类型
     */
    private int templateType;
    private static final int REQUEST_CHANGE_RESOLUTION = 2;

    /**
     * 屏幕共享控件
     */
    private SuspensionVideoView shareVideoView;
    /**
     * 辅助摄像头控件
     */
    private SuspensionVideoView assistVideoView;
    /**
     * 插播音视频控件
     */
    protected InterCutVideoView interCutVideoView;
    private SparseIntArray mActions = new SparseIntArray();
    protected CCUser mCurUser;
    protected BottomCancelPopup mUserPopup;

    private boolean isHandUp = false; // 是否有人举手

    /**
     * 奖杯视图
     */
    private CupView cupView;
    // 用户相关操作
    private static final int FLAG_KICK_OUT_LIANMAI = 0x1000;
    private static final int FLAG_GAG = 0x1001;
    private static final int FLAG_CANCEL_GAG = 0x1002;
    private static final int FLAG_KICK_OUT = 0x1003;
    private static final int FLAG_ALLOW_MAI = 0x1004;
    private static final int FLAG_INVITE_MAI = 0x1005;
    private static final int FLAG_INVITE_CANCEL = 0x1006;
    private static final int FLAG_TOGGLE_MIC = 0x1007;
    private static final int FLAG_DRAW_DOC = 0x1008;
    private static final int FLAG_PULL_MAI = 0x1009;
    private static final int FLAG_SETUP_TEACHER = 0x1010;
    private static final int FLAG_CUP = 0x1011;
    private static final int FLAG_TALKER_FLOWER = 0x1012;
    private static final int FLAG_ASSISTANT_UP_SPEAK = 0x1013;
    private static final int FLAG_ASSISTANT_DOWN_SPEAK = 0x1014;

    protected static final int MIN_CLICK_DELAY_TIME = 180000;
    protected static long lastClickTime = 0;

    private AtomicInteger mHandupCount = new AtomicInteger(0);

    private BottomIconPopup mIconPopup;
    private BottomAssistantIconPopup mAssistantIconPopup;
    private View mRoot;

    @Override
    protected int getLayoutId() {
        if (sClassDirection == 0) {
            return R.layout.activity_teacher_room_layout;
        } else {
            return R.layout.activity_teacher_room_layout_h;
        }
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(CCUser user) {
        if (user.getUserRole() == CCAtlasClient.AUDITOR) {
            mClickAuditorId = user.getUserId();
            showAuditor();
        } else {
            updateOrShowUserPopup(user);
        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(sClassDirection==1){
            AndroidBug5497Workaround.assistActivity(this);
        }
        initView();
        initListener();

    }


    private void initView() {
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
        initVideoViewManager();

        initUserPopup();
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
                showCloseDialog();
            }

            @Override
            public void videoController(boolean isVideoShow) {
                mVideoManager.setRecyclerViewVisibility(isVideoShow ? View.VISIBLE : View.GONE);
            }

            @Override
            public void videoFollow() {
                showLoading();
                String userid;
                if (menuTopView.isVideoFollow()) {
                    userid = "";
                } else {
                    userid = ((MainVideoManager) mVideoManager).getMainVideoUserid();
                }
                mCCBarLeyManager.changeMainStreamInSigleTemplate(
                        userid, new CCBarLeyCallBack<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dismissLoading();
                                menuTopView.setFollowEnable(!TextUtils.isEmpty(mCCAtlasClient.teacherFollowUserID()));

                            }

                            @Override
                            public void onFailure(String err) {
                                dismissLoading();
                                showToast(err);
                            }
                        });
            }

            @Override
            public void onClickUser(CCUser user, int position) {
                if(!mCCAtlasClient.isRoomLive()){
                    return;//未直播不显示操作栏
                }
                if (position == 0) {
                    if (user.getUserRole() == CCAtlasClient.TALKER) {
                        updateOrShowUserPopup(user);
                    }
                    return;
                }

                if (user.getUserRole() == CCAtlasClient.ASSISTANT) {
                    AssistShowToTeacherPopup(user);
                } else {
                    updateOrShowUserPopup(user);
                }
            }
        });

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

        //预览相关
        mSelfStreamView = new VideoStreamView();
        mSelfRenderer = new SurfaceView(this);
//        mSelfRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//        mCCAtlasClient.initSurfaceContext(mSelfRenderer);
//        mSelfStreamView.setRenderer(mSelfRenderer);

        if (mCCAtlasClient.getInteractBean() != null) {
            SubscribeRemoteStream selfStream = new SubscribeRemoteStream();
            selfStream.setUserName(mCCAtlasClient.getInteractBean().getUserName());
            selfStream.setUserId(mCCAtlasClient.getUserIdInPusher());
            selfStream.setAllowAudio(mCCAtlasClient.getInteractBean().getUserSetting().isAllowAudio());
            selfStream.setAllowVideo(mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo());
            selfStream.setLock(mCCAtlasClient.getInteractBean().isLock());
            selfStream.setUserRole(PRESENTER);
            mSelfStreamView.setStream(selfStream);
        }
        TeacherRoomActivityPermissionsDispatcher.startPreviewWithPermissionCheck(this);

        //聊天发送
        setChatView(R.id.room_chat_cv);
        //底部菜单
        menuBottomView = findViewById(R.id.menu_bottom_rl);
        menuBottomView.setListener(this, new MenuBottomTeacherView.MenuBottomTeacherListener() {
            @Override
            public void menuOpenChat() {
                chatView.openChat();
            }

            @Override
            public void startLive() {
                initLiveActionPopup();
            }

            @Override
            public void clickMore() {
                initMoreMenu();
                menuMoreTeacherView.show();

            }

            @Override
            public void clickCamera() {
                if (mCCAtlasClient.getInteractBean() != null &&
                        mCCAtlasClient.getInteractBean().getUserSetting().isAllowVideo()) {
                    if (cameraDialog == null) {
                        ArrayList<String> menuText = new ArrayList<>();
                        menuText.add("切换摄像头");
                        menuText.add("关闭摄像头");
                        cameraDialog = new BottomMenuDialog(mContext, menuText, true);
                        cameraDialog.setOnMenuItemClickListener(new BottomMenuDialog.OnMenuItemClickListener() {
                            @Override
                            public void onClick(String text, int index) {
                                if (index == 1) {
                                    mCCAtlasClient.disableVideo(true);
//                                    mSelfRenderer.cleanFrame();
                                    menuBottomView.setCameraEnable(false);
                                } else if (index == 0) {
                                    mCCAtlasClient.switchCamera(new CCAtlasCallBack<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            Tools.log(TAG, "=====switchCamera=====onSuccess=====" + aBoolean);
                                        }

                                        @Override
                                        public void onFailure(int errCode, String errMsg) {
                                            showToast(errMsg);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    cameraDialog.show();
                } else {
                    mCCAtlasClient.enableVideo(true);
                    menuBottomView.setCameraEnable(true);

                }


            }

            @Override
            public void clickStopLive() {
                mExitAction = EXIT_ACTION_STOP;
                stopLiveDialog = new CustomTextViewDialog(mContext);
                stopLiveDialog.setMessage("是否确认结束直播？");
                stopLiveDialog.setBtn("取消", "确认", new CustomTextViewDialog.CustomClickListener() {
                    @Override
                    public void onLeftClickListener() {

                    }

                    @Override
                    public void onRightClickListener() {
                        exitRoom();

                    }

                    @Override
                    public void onOneBtnClickListener() {

                    }
                });
            }

            @Override
            public void clickMic() {
                if (mCCAtlasClient.getInteractBean() != null &&
                        !mCCAtlasClient.getInteractBean().getUserSetting().isAllowAudio()) {
                    mCCAtlasClient.enableAudio(true);
                    menuBottomView.setMicEnable(true);

                } else {
                    mCCAtlasClient.disableAudio(true);
                    menuBottomView.setMicEnable(false);
                }
            }


        });

        //奖杯视图
        cupView = findViewById(R.id.room_cup_view);
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
        menuTopView.setVideoFollowShow(templateType == CCAtlasClient.TEMPLATE_SINGLE);
        if (sClassDirection == 1 && templateType == CCAtlasClient.TEMPLATE_SPEAK) {
            menuTopView.setVideoControllerShown(true);
        }

    }

    private void showCloseDialog() {
        mExitAction = EXIT_ACTION_CLOSE;
        if (isPublish) {
            showExitDialog("是否确认离开课堂？离开后将结束直播");
        } else {
            showExitDialog("是否确认离开课堂？");
        }
    }

    private void initMoreMenu() {
        if (menuMoreTeacherView == null) {
            menuMoreTeacherView = findViewById(R.id.menu_more_teacher_view);
            menuMoreTeacherView.initData(mContext, isMoreItemHasDoc, sClassDirection, new MenuMoreTeacherView.MenuMoreListener() {
                @Override
                public void rollCall() {
                    if (!mCCAtlasClient.isRoomLive()) {
                        showToast("直播未开始");
                        return;
                    }
                    if (NamedActivity.mNamedTimeEnd != 0) {
                        NamedCountActivity.startSelf(mContext,
                                (int) (NamedActivity.mNamedTimeEnd - System.currentTimeMillis()) / 1000);
                    } else {
                        go(NamedActivity.class);
                    }
                }

                @Override
                public void updateImage() {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, Config.REQUEST_SYSTEM_PICTURE_UPDATE);
                }

                @Override
                public void clickDoc() {
                    go(DocListActivity.class, Config.TEACHER_REQUEST_CODE);
                }

                @Override
                public void clickSetting() {
                    go(SettingActivity.class, REQUEST_CHANGE_RESOLUTION);
                }
            });
        }
    }


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startPreview() {
        try {
//            mCCAtlasClient.setCameraType(true);
//            mCCAtlasClient.setAppOrientation(sClassDirection == 0);//true横屏竖屏
//            mCCAtlasClient.setMirror(mSelfRenderer,false);
//            mCCAtlasClient.createLocalStream(mContext, mCCAtlasClient.getMediaMode(), true);
//            mCCAtlasClient.attachLocalCameraStram(mSelfRenderer);
//            mCCAtlasClient.setLocalVideoMirror(true);
            mCCAtlasClient.setAppOrientation(sClassDirection == 0);//true横屏竖屏
            //设置分辨率
            mCCAtlasClient.setResolution(SPUtil.getIntsance().getInt(TEACHER_RESOLUTION, mCCAtlasClient.getDefaultResolution()));
            //开启预览
            SurfaceView surfaceView = mCCAtlasClient.startPreview(this,com.bokecc.stream.config.Config.RENDER_MODE_FIT);
            mSelfStreamView.setSurfaceViewList(surfaceView);
            //添加到视频列表中
            mVideoManager.addVideoView(mSelfStreamView);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }



    private void initListener() {
        // 是否开播
        if (mCCAtlasClient.isRoomLive()) {
            isPublish = true;
            //开始进来首先获取流对象，直接去订阅
            mCCAtlasClient.setSubscribeRemoteStreams();
            menuBottomView.setLiveStatus(true);
            initLiveActionPopup();
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
            templateType = mCCAtlasClient.getInteractBean().getTemplate();
            isMoreItemHasDoc = false;
            switch (templateType) {
                case CCAtlasClient.TEMPLATE_SPEAK:
                    isMoreItemHasDoc = true;
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

                        }
                    });
                    initDocView();
                    break;
                case CCAtlasClient.TEMPLATE_SINGLE:
                    mVideoManager = new MainVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_main_layout));


                    break;
                case CCAtlasClient.TEMPLATE_TILE:
                case CCAtlasClient.TEMPLATE_DOUBLE_TEACHER:
                    mVideoManager = new TileVideoManager(this, sClassDirection, (ViewStub) findViewById(R.id.view_video_tile_layout));
                    break;

            }
            if(mVideoManager!=null){
                mVideoManager.setVideoClickListener(this);
            }
        } catch (Exception e) {
           Tools.handleException(TAG,e);
        }
    }

    private void initAssistantIconPopup() {
        mAssistantIconPopup = new BottomAssistantIconPopup(this);
        mAssistantIconPopup.setOutsideCancel(true);
        mAssistantIconPopup.setKeyBackCancel(true);
        mAssistantIconPopup.setOnChooseClickListener(new BottomAssistantIconPopup.OnChooseClickListener() {
            @Override
            public void onClick(int index, int tag, VideoStreamView videoStreamView) {
                if (tag == R.mipmap.close_camera_normal) {
                    mCCAtlasClient.toggleVideo(false, videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.open_camera_normal) {
                    mCCAtlasClient.toggleVideo(true, videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.close_mic_normal) {
                    mCCAtlasClient.toggleAudio(false, videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.open_mic_normal) {
                    mCCAtlasClient.toggleAudio(true, videoStreamView.getStream().getUserId());
                }
            }
        });
    }

    private void initLiveActionPopup() {
        final int manualRecord = CCAtlasClient.getInstance().getInteractBean().getManualRecord();
        if (mCCAtlasClient.isRoomLive()) {
            Tools.log(TAG, "已经开播。。。");
            if (endLastLiveDialog == null) {
                endLastLiveDialog = new CustomTextViewDialog(mContext);
                endLastLiveDialog.setMessage("是否继续上场直播");
                endLastLiveDialog.setBtn("终止", "继续", new CustomTextViewDialog.CustomClickListener() {
                    @Override
                    public void onLeftClickListener() {
                        stopLastLive();
                    }

                    @Override
                    public void onRightClickListener() {
                        continueLastLive(1);
                    }

                    @Override
                    public void onOneBtnClickListener() {

                    }
                });
            } else {
                endLastLiveDialog.show();
            }

        } else if (manualRecord == 1) {
            Tools.log(TAG, "手动录制，未直播。。。");
            if (recordDialog == null) {
                recordDialog = new CustomTextViewDialog(mContext);
                recordDialog.setMessage("是否开启录制功能");
                recordDialog.setBtn("取消", "开启", new CustomTextViewDialog.CustomClickListener() {
                    @Override
                    public void onLeftClickListener() {
                        continueLastLive(0);
                    }

                    @Override
                    public void onRightClickListener() {
                        //开启录制功能
                        continueLastLive(1);
                    }

                    @Override
                    public void onOneBtnClickListener() {

                    }
                });
            } else {
                recordDialog.show();
            }


        } else {
            Tools.log(TAG, "非手动录制，未开播。。。");
            if (startLiveDialog == null) {
                startLiveDialog = new CustomTextViewDialog(mContext);
                startLiveDialog.setMessage("直播即将开始");
                startLiveDialog.setBtn("取消", "确定", new CustomTextViewDialog.CustomClickListener() {
                    @Override
                    public void onLeftClickListener() {
//                            stopLastLive();
                    }

                    @Override
                    public void onRightClickListener() {
                        continueLastLive(1);
                    }

                    @Override
                    public void onOneBtnClickListener() {

                    }
                });
            } else {
                startLiveDialog.show();
            }

        }

    }

    /**
     * 停止上一场直播
     */
    private void stopLastLive() {
        showLoading();
        mCCAtlasClient.stopLive(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoading();
                showToast("成功终止上一场异常停止直播");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissLoading();
                showToast(errMsg);
            }

        });
    }

    /**
     * 停止推流
     * @param flag
     */
    private synchronized void unpublish(final boolean flag) {
        showLoading();
        mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tools.log(TAG, "==unpublish==onSuccess:" + Thread.currentThread());
                dismissLoading();
                isPublish = false;
                mNamedTimeEnd = 0L; // 更新点名结束时间
                if (flag) {
                    leaveHome();
                } else {
                    menuBottomView.setLiveStatus(false);
                    menuTopView.showHandupIcon(false);
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.log(TAG, "errCode:" + errCode + ",errMsg：" + errMsg);
                dismissLoading();
                if (flag) {
                    leaveHome();
                }
            }
        });
    }

    /**
     * 初始化文档视图
     */
    private void initDocView() {
        if (ccDocView == null) {
            ccDocView = findViewById(R.id.id_lecture_video_doc);
            //初始化文档
            ccDocView.initRole(mContext, PRESENTER, sClassDirection);
            //设置文档背景颜色，默认为白色
//            ccDocView.setDocBackgroundColor("#cfe2f3");
            //设置文档相关监听
            ccDocView.setDocHandleListener(new CCDocView.IDocHandleListener() {

                @Override
                public void exitDocFullScreen() {
                    chatView.setVisibility(View.VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    menuTopView.setVisibility(View.VISIBLE);
                    menuBottomView.setVisibility(View.VISIBLE);
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



    /**
     * 继续上一场直播
     */
    private void continueLastLive(int isRecord) {
        // 发布本地流
        mCCAtlasClient.startLive(isRecord, new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                publish();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast(errMsg);
            }
        });

    }

    /**
     * 发布本地流
     */
    private synchronized void publish() {
        showLoading();
        mCCAtlasClient.publish(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tools.log(TAG, "thread:" + Thread.currentThread() + ", onSuccess: [ " + mCCAtlasClient.getLocalStreamId() + " ]");
                dismissLoading();
                if (templateType == CCAtlasClient.TEMPLATE_SPEAK) {
                    try {
                        if (ccDocView != null) {
                            ccDocView.sendCurrentDocPage();
                        }

                    } catch (Exception e) {
                        Tools.handleException(TAG, e);
                    }
                }
                menuBottomView.setLiveStatus(true);
                isPublish = true;
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissLoading();
                showToast(errMsg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        if (event.what != Config.INTERACT_EVENT_WHAT_USER_COUNT) {
            Tools.log(LogConfig.onInteractEventLog, "" + Tools.intToHex(event.what));
        }
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_CHAT:
                chatView.updateChatList((ChatEntity) event.obj);
                chatView.setChatViewNoneBg(false);
                break;
            case Config.INTERACT_EVENT_WHAT_ERROR:
                showToast((String) event.obj);
            case Config.INTERACT_EVENT_WHAT_USER_COUNT:
                int mCount = (Integer) event.obj + (Integer) event.obj2;
                menuTopView.setUserCount(mCount);
                break;
            case Config.INTERACT_EVENT_WHAT_HANDUP:
                boolean isHandup = (boolean) event.obj2;
                if (isHandup) {
                    mHandupCount.incrementAndGet();
                } else {
                    if (mHandupCount.get() != 0) {
                        mHandupCount.decrementAndGet();
                    }
                }
                isHandUp = mHandupCount.get() > 0;
                menuTopView.showHandupIcon(isHandUp);
                break;
            case Config.INTERACT_EVENT_WHAT_QUEUE_MAI:
                ArrayList<CCUser> users = (ArrayList<CCUser>) event.obj; // 重新赋值
                if (mUserPopup.isShowing()) { // 如果弹出框显示，更新当前选中的用户状态
                    for (CCUser user :
                            users) { // 刷新
                        if (mCurUser != null && user.getUserId().equals(mCurUser.getUserId())) {
                            updateOrShowUserPopup(user);
                        }
                    }
                }
                if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean()
                        .getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_NAMED) { // 点名连麦模式
                    boolean flag = false;
                    for (CCUser user :
                            users) {
                        if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI) {
                            flag = true;
                            break;
                        }
                        if (mUserPopup.isShowing()) { // 如果弹出框显示，更新当前选中的用户状态
                            if (mCurUser != null && user.getUserId().equals(mCurUser.getUserId())) {
                                updateOrShowUserPopup(user);
                            }
                        }
                    }
                    if (flag != isHandUp) {
                        isHandUp = flag;
                        menuTopView.showHandupIcon(flag);
                    }
                    if (flag) {
                        if (ccDocView != null && sClassDirection == 0 && !ccDocView.isDocFullScreen())
                            ccDocView.handleClickRootView();
                    }

                }
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_ADDED:
                try {
                    EventBus.getDefault().removeStickyEvent(event);
                    final SubscribeRemoteStream stream = (SubscribeRemoteStream) event.obj;
                    if (stream.getRemoteStream().isScreenStream()) {//桌面共享
                        mCCAtlasClient.SubscribeStream(stream.getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT, new CCAtlasCallBack<CCStream>() {
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
                                showToast(errMsg);
                            }
                        });
                    } else if (stream.getRemoteStream().getHasImprove()) {//辅助摄像头
                        mCCAtlasClient.SubscribeStream(stream.getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT, new CCAtlasCallBack<CCStream>() {
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
                                showToast(errMsg);
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_REMOVED:
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
                showExitDialogOneBtn("流发生错误");
                break;

            case Config.INTERACT_EVENT_WHAT_ANSWER_NAMED:
                if (NamedActivity.mNamedTimeEnd != 0L) {
                    NamedActivity.mNamedCount++;
                }
                break;
            case Config.INTERACT_EVENT_WHAT_USER_AUDIO://学生麦克风摄像头状态被动变化监听事件
                if (mVideoManager != null)
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, 0);
                break;
            case Config.INTERACT_EVENT_WHAT_USER_VIDEO://学生摄像头状态被动变化监听事件
                if (mVideoManager != null)
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, 1);
                break;
            case Config.INTERACT_EVENT_WHAT_AUTH_DRAW:
                if (mVideoManager != null)
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, 2);
                break;
            case Config.INTERACT_EVENT_WHAT_LOCK:
                if (mVideoManager != null)
                    mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, 3);
                break;
            case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER:
                mVideoManager.updateVideos((String) event.obj, (Boolean) event.obj2, 4);
                break;
            case Config.INTERACT_EVENT_WHAT_TEACHER_DOWN:
                showExitDialogOneBtn("老师流异常remove");
                break;
            case Config.INTERACT_EVENT_WHAT_TEMPLATE:
//                setSelected((Integer) event.obj);
                break;
            case Config.INTERACT_EVENT_WHAT_DEVICE_FAIL:
                showToast(event.obj2 + " 连麦设备不可用，上麦失败");
                break;
            case Config.INTERACT_EVENT_WHAT_TEACHER_SETUPTHEACHER_FLAG:
                if (ccDocView != null)
                    ccDocView.teacherSetupTeacherPage((int) event.obj);
                break;
//            case Config.INTERACT_EVENT_WHAT_PAGECHANGE:
//                if (ccDocView != null)
//                    ccDocView.setPageChange((int) event.obj, (Boolean) event.obj2);
//                break;
            case Config.INTERACT_EVENT_WHAT_DOC_CHANGE:
                if (ccDocView != null)
                    ccDocView.setAssistDocInfo((DocInfo) event.obj, (int)event.obj2, true);
                break;
            case Config.INTERACT_EVENT_WHAT_KICK_OUT:
                isKick = true;
                showExitDialogOneBtn("对不起，您已经被踢出该直播间");

            case Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT:
                showExitDialogOneBtn("当前用户掉线了");
                break;
            case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START:
                startClass();
                break;
            case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP:
                endClass();
                break;

            case Config.INTERACT_EVENT_WHAT_SEND_CUP:
                SendReward cup = (SendReward) event.obj;
                setOnUserCupCountListener(new StudentRoomActivity.OnUserCupCountListener() {
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
            case Config.INTERACT_EVENT_WHAT_UP_MAI:
                if (CCAtlasClient.getInstance().getInteractBean().getAssistantSwitch() == 1) {
                    if (mCCAtlasClient.getUserList() != null) {
                        for (int i = 0; i < mCCAtlasClient.getUserList().size(); i++) {
                            if (mCCAtlasClient.getUserList().get(i).getUserRole() == ASSISTANT) {
                                Tools.log(TAG, "INTERACT_EVENT_WHAT_UP_MAI ");
                                startPreview();
                                mCCAtlasClient.switchOnPublish(new CCAtlasCallBack<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Tools.log(TAG, "switchOnPublish onSuccess");
                                    }

                                    @Override
                                    public void onFailure(int errCode, String errMsg) {
                                        Tools.log(TAG, "switchOnPublish onFailure errMsg" + errMsg + ";errCode=" + errCode);
                                    }
                                });

                            }
                        }
                    }
                }
                break;
            case Config.INTERACT_EVENT_WHAT_DOWN_MAI:
                if (CCAtlasClient.getInstance().getInteractBean().getAssistantSwitch() == 1) {
                    Tools.log(TAG, "wdh--down--INTERACT_EVENT_WHAT_DOWN_MAI ");
                    if (mCCAtlasClient.getUserList() != null) {
                        for (int i = 0; i < mCCAtlasClient.getUserList().size(); i++) {
                            if (mCCAtlasClient.getUserList().get(i).getUserRole() == ASSISTANT) {
                                mCCAtlasClient.switchSpeakUnpublish(new CCAtlasCallBack<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mVideoStreamViews.remove(mSelfStreamView);
                                        mVideoManager.removeVideoView(mSelfStreamView);
//                                        mCurFragment.notifySelfRemove(mSelfStreamView);
                                    }

                                    @Override
                                    public void onFailure(int errCode, String errMsg) {

                                    }
                                });
                            }
                        }
                    }
                }
                break;
            case Config.INTERACT_EVENT_WHAT_FORCE_OUT:
                showExitDialogOneBtn("对不起，您已经被挤出该直播间");
                break;
            case Config.INTERACT_EVENT_WHAT_ATLAS_SERVER_DISCONNECTED:
                showExitDialogOneBtn("网络已经断开，已经连麦的自动下麦！");
                break;
            case Config.INTERACT_EVENT_WHAT_SWITCH_SPEAK_ON:
                Tools.log(TAG, "INTERACT_EVENT_WHAT_SWITCH_SPEAK_ON: ");
                startPreview();
                break;
            case Config.INTERACT_EVENT_WHAT_SWITCH_SPEAK_OFF:
                Tools.log(TAG, "INTERACT_EVENT_WHAT_SWITCH_SPEAK_OFF: ");
                mVideoStreamViews.remove(mSelfStreamView);
                mVideoManager.removeVideoView(mSelfStreamView);
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_START_OPT:
                showLoading();
                //移除预览
                mVideoStreamViews.remove(mSelfStreamView);
                mVideoManager.removeVideoView(mSelfStreamView);
//                mCurFragment.notifyItemChanged(mSelfStreamView, 0, false);
                //摄像头，麦克风状态恢复
                menuBottomView.setCameraEnable(true);
                menuBottomView.setMicEnable(true);
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_RELOAD_PREVIEW:
                TeacherRoomActivityPermissionsDispatcher.startPreviewWithPermissionCheck(TeacherRoomActivity.this);
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_STOP_OPT:
                dismissLoading();
                break;
            case Config.INTERACT_EVENT_WHAT_STREAM_SWITCH_ERROR:
                dismissLoading();
                showExitDialogOneBtn("优化线路失败，请重新进入房间！");
                break;
        }
    }

    private void endClass() {
        try {

            if (ccDocView != null) {
                ccDocView.stopClass();
            }
            menuBottomView.setLiveStatus(false);
            menuTopView.showHandupIcon(false);

        } catch (Exception e) {
            Tools.handleException(TAG, e);
        }
    }

    /**
     * 开始上课
     */
    private void startClass() {
        if (sClassDirection == 1 && templateType == CCAtlasClient.TEMPLATE_SPEAK) {
            //  更新图标
            menuTopView.setVideoControllerShown(true);
        }
//        if (templateType == CCAtlasClient.TEMPLATE_SPEAK && (sClassDirection == 1 || (ccDocView != null && ccDocView.isDocFullScreen()))) {
//            ccDocView.setDrawUtilShow(true);
//        }
        /*if (templateType == CCAtlasClient.TEMPLATE_SPEAK && ccDocView != null) {
            ccDocView.setClassStatus(true);
        }*/
        menuBottomView.setLiveStatus(true);
        /*
        if (CCAtlasClient.getInstance().getInteractBean().getManualRecord() == 1) {
            if (mCurFragment instanceof LectureFragment) {
                try {
                    if (CCApplication.sClassDirection == 1 && mCCAtlasClient.getInteractBean() != null &&
                            mCCAtlasClient.getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SPEAK) {
                        ((LectureFragment) mCurFragment).authDraw(true, 0);
                        mDrawLayout.setVisibility(View.VISIBLE);
                        if (((LectureFragment) mCurFragment).isWhitboard()) {
                            mPageChangeLayout.setVisibility(View.GONE);
                        } else {
                            mPageChangeLayout.setVisibility(View.VISIBLE);
                        }
                    } else if (CCApplication.sClassDirection == 0 && mCCAtlasClient.getInteractBean() != null &&
                            mCCAtlasClient.getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SPEAK) {
                        ((LectureFragment) mCurFragment).authDraw(true, 0);
                    }
                } catch (Exception e) {
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStartLayout.setVisibility(View.GONE);
                    mStopLayout.setVisibility(View.VISIBLE);
                    if (mCCAtlasClient.getInteractBean() != null &&
                            mCCAtlasClient.getInteractBean().getManualRecord() == 1) {
                        mFrameRecorderImage.setVisibility(View.GONE);
                        mRecorderImage.setVisibility(View.VISIBLE);
                    } else {
                        mFrameRecorderImage.setVisibility(View.GONE);
                        mRecorderImage.setVisibility(View.GONE);
                    }
                    mMic.setBackgroundResource(R.drawable.mic_selector);
                    mCamera.setBackgroundResource(R.drawable.camera_selector);
                }
            });
        }
        if (CCApplication.sClassDirection == 1 && mChatEntities.size() > 0) {
            mChatList.setBackgroundResource(R.drawable.shape_chat_bg);
        } else {
            mChatList.setBackgroundResource(R.color.chat_bg);
        }*/
        if (ccDocView != null) {
            ccDocView.startClass();
        }
        setChatViewBg();
        /*if (mCCAtlasClient.getRole() == CCAtlasClient.PRESENTER) {
            if (mCurFragment instanceof LectureFragment) {
                mCurFragment.classStop();
            }
        }*/
    }
    /**
     *权限回调，调用PermissionsDispatcher的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        TeacherRoomActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    /**
     * 系统界面的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ccDocView != null) {
            ccDocView.onActivityResult(requestCode, resultCode, data);
        }
        /*if(requestCode == REQUEST_CHANGE_RESOLUTION && resultCode == RESULT_OK){
            if (mCurFragment instanceof MainVideoFragment) {
                ((MainVideoFragment) mCurFragment).setUpView();
            }
        }*/
    }

    /**
     * 老师点击学生显示学生弹框
     *
     * @param user
     */
    protected void updateOrShowUserPopup(CCUser user) {
        mCurUser = user;
        mActions.clear();
        mUserPopup.clear();
        int userPopupIndex = 0;
        mUserPopup.add(userPopupIndex, "奖励奖杯");
        mActions.put(userPopupIndex++, FLAG_CUP);

        mUserPopup.add(userPopupIndex, user.getUserSetting().isAllowDraw() ? "取消授权标注" : "授权标注");
        mActions.put(userPopupIndex++, FLAG_DRAW_DOC);

        if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
            mUserPopup.add(userPopupIndex, "踢下麦");
            mActions.put(userPopupIndex++, FLAG_KICK_OUT_LIANMAI);
        } else {
            if (user.getLianmaiStatus() != CCAtlasClient.LIANMAI_STATUS_UP_MAI &&
                    mCCAtlasClient.getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_AUTO) {
                mUserPopup.add(userPopupIndex, "拉上麦");
                mActions.put(userPopupIndex++, FLAG_PULL_MAI);
            }
        }
        if (mCCAtlasClient.getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_NAMED) {
            if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI) {
                mUserPopup.add(userPopupIndex, "同意上麦");
                mActions.put(userPopupIndex++, FLAG_ALLOW_MAI);
            }
            if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IDLE) {
                mUserPopup.add(userPopupIndex, "邀请上麦");
                mActions.put(userPopupIndex++, FLAG_INVITE_MAI);
            }
            if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_INVITE_MAI) {
                mUserPopup.add(userPopupIndex, "取消邀请");
                mActions.put(userPopupIndex++, FLAG_INVITE_CANCEL);
            }
        }
        if (user.getUserSetting().isAllowChat()) {
            mUserPopup.add(userPopupIndex, "禁言");
            mActions.put(userPopupIndex++, FLAG_GAG);
        } else {
            mUserPopup.add(userPopupIndex, "取消禁言");
            mActions.put(userPopupIndex++, FLAG_CANCEL_GAG);
        }
        mUserPopup.add(userPopupIndex, "踢出房间");
        mActions.put(userPopupIndex++, FLAG_KICK_OUT);

        mUserPopup.add(userPopupIndex, user.getUserSetting().isSetupTeacher() ? "撤销讲师" : "设为讲师");
        mActions.put(userPopupIndex, FLAG_SETUP_TEACHER);

        if (!mUserPopup.isShowing()) {

            mUserPopup.show(menuTopView.getShowRootView());
        }

    }

    private void initUserPopup() {
        mUserPopup = new BottomCancelPopup(this);
        mUserPopup.setOutsideCancel(true);
        mUserPopup.setKeyBackCancel(true);
        ArrayList<String> datas = new ArrayList<>();
        mUserPopup.setChooseDatas(datas);
        mUserPopup.setOnChooseClickListener(new BottomCancelPopup.OnChooseClickListener() {
            @Override
            public void onClick(int index) {
                dealWithUserPopupAction(mActions.get(index));
            }
        });
    }

    private void dealWithUserPopupAction(int action) {
        switch (action) {
            case FLAG_DRAW_DOC:
                if (mCurUser.getUserSetting().isAllowDraw()) {
                    mCCDocViewManager.cancleAuthUserDraw(mCurUser.getUserId());
                } else {
                    mCCDocViewManager.authUserDraw(mCurUser.getUserId());
                }
                break;
            case FLAG_SETUP_TEACHER:
                if (mCurUser.getUserSetting().isSetupTeacher()) {
                    mCCDocViewManager.cancleAuthUserTeacher(mCurUser.getUserId());
                } else {
                    mCCDocViewManager.authUserTeacher(mCurUser.getUserId());
                }
                break;
            case FLAG_KICK_OUT_LIANMAI:
                showLoading();
                mCCBarLeyManager.kickUserFromSpeak(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        showToast(err);
                    }
                });
                break;
            case FLAG_TOGGLE_MIC:
                mCCAtlasClient.toggleAudio(!mCurUser.getUserSetting().isAllowAudio(), mCurUser.getUserId());
                break;
            case FLAG_GAG:
                mCCAtlasClient.gagOne(true, mCurUser.getUserId());
                break;
            case FLAG_CANCEL_GAG:
                mCCAtlasClient.gagOne(false, mCurUser.getUserId());
                break;
            case FLAG_KICK_OUT:
                mCCBarLeyManager.kickUserFromRoom(mCurUser.getUserId());
                break;
            case FLAG_ALLOW_MAI:
                if (mCurUser.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI) {
                    showLoading();
                    mCCBarLeyManager.certainHandup(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            showToast(err);
                        }
                    });
                } else {
                    showToast("无效操作");
                }
                break;
            case FLAG_INVITE_MAI:
                if (mCurUser.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IDLE) {
                    showLoading();
                    mCCBarLeyManager.inviteUserSpeak(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            showToast(err);
                        }
                    });
                } else if (mCurUser.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI) {
                    showLoading();
                    mCCBarLeyManager.certainHandup(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            showToast(err);
                        }
                    });
                }
                break;
            case FLAG_INVITE_CANCEL:
                if (mCurUser.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_INVITE_MAI) {
                    showLoading();
                    mCCBarLeyManager.cancleInviteUserSpeak(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            showToast(err);
                        }
                    });
                }
                break;
            case FLAG_PULL_MAI:
                showLoading();
                mCCBarLeyManager.certainHandup(mCurUser.getUserId(), new CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(String err) {
                        dismissLoading();
                        showToast(err);
                    }
                });
                break;
            case FLAG_CUP:
                if (mCCAtlasClient.isRoomLive()) {
                    mCCAtlasClient.ccSendCupData(mCurUser.getUserId(), mCurUser.getUserName());
                } else {
                    Toast toast = Toast.makeText(this, "直播未开始，不能使用此功能", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case FLAG_TALKER_FLOWER:
                long curClickTime = System.currentTimeMillis();
                if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                    // 超过点击间隔后再将lastClickTime重置为当前点击时间
                    lastClickTime = curClickTime;
                    mCCAtlasClient.ccSendFlowerData();
                } else {
                    if (mCCAtlasClient.isRoomLive()) {
                        Toast toast = Toast.makeText(this, "鲜花生长中，3分钟后才可以送出哟！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(this, "直播未开始，不能使用此功能", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }
                break;
            case FLAG_ASSISTANT_UP_SPEAK://助教自己上麦
               /* if (mCCAtlasClient.isRoomLive()) {
                    showLoading();
                    CCApplication.isSpeakStatus = 0;
                    mCCAtlasClient.publish(false, new CCAtlasCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();

                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissLoading();
                            Tools.showToast(errMsg);
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(this, "直播未开始，不能点击上麦按钮", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }*/
                break;

        }
        mCurUser = null;
    }

    /**
     * 助教点击老师，显示老师弹框
     *
     * @param user
     */
    protected void AssistShowToTeacherPopup(CCUser user) {
        mCurUser = user;
        mActions.clear();
        mUserPopup.clear();
        int userPopupIndex = 0;

//        mUserPopup.add(userPopupIndex, "下麦");
//        mActions.put(userPopupIndex++, FLAG_TEACHER_KICKOUT_ASSIST);

        if (user.getUserSetting().isAllowChat()) {
            mUserPopup.add(userPopupIndex, "禁言");
            mActions.put(userPopupIndex++, FLAG_GAG);
        } else {
            mUserPopup.add(userPopupIndex, "取消禁言");
            mActions.put(userPopupIndex++, FLAG_CANCEL_GAG);
        }

        mUserPopup.add(userPopupIndex, "踢出房间");
        mActions.put(userPopupIndex, FLAG_KICK_OUT);

        if (!mUserPopup.isShowing())
            mUserPopup.show(menuTopView.getShowRootView());
    }

    private void initIconPopup() {
        mIconPopup = new BottomIconPopup(this);
        mIconPopup.setOutsideCancel(true);
        mIconPopup.setKeyBackCancel(true);
        mIconPopup.setOnChooseClickListener(new BottomIconPopup.OnChooseClickListener() {
            @Override
            public void onClick(int index, int tag, final VideoStreamView videoStreamView) {
                if (tag == R.mipmap.auth_draw_normal) {
                    mCCDocViewManager.authUserDraw(videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.auth_draw_cancel_normal) {
                    mCCDocViewManager.cancleAuthUserDraw(videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.setup_teacher) {
                    mCCDocViewManager.authUserTeacher(videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.setup_teacher_canclenormal) {
                    mCCDocViewManager.cancleAuthUserTeacher(videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.close_camera_normal) {
                    mCCAtlasClient.toggleVideo(false, videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.open_camera_normal) {
                    mCCAtlasClient.toggleVideo(true, videoStreamView.getStream().getUserId());
                } else if (tag == R.mipmap.close_mic_normal) {
//                        mCCAtlasClient.toggleAudio(false, videoStreamView.getStream().getUserId());
                        //普通使用请直接走上面一行代码逻辑即可，如需添加权限控制请走下面逻辑
                        mCCAtlasClient.mediaSwitchAudioUserid(false,videoStreamView.getStream().getUserId(),videoStreamView.getStream().getUserRole(),new CCAtlasCallBack<String>(){
                            @Override
                            public void onSuccess(String object) {
                                mCCAtlasClient.toggleAudio(false, videoStreamView.getStream().getUserId());
                            }

                            @Override
                            public void onFailure(int errCode, String errMsg) {
                                showToast(errMsg);
                            }
                        });
                }  else if (tag == R.mipmap.open_mic_normal) {
//                    mCCAtlasClient.toggleAudio(true, videoStreamView.getStream().getUserId());
                    //普通使用请直接走上面一行代码逻辑即可，如需添加权限控制请走下面逻辑
                    mCCAtlasClient.mediaSwitchAudioUserid(true,videoStreamView.getStream().getUserId(),videoStreamView.getStream().getUserRole(),new CCAtlasCallBack<String>(){
                        @Override
                        public void onSuccess(String object) {
                            mCCAtlasClient.toggleAudio(true, videoStreamView.getStream().getUserId());

                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            showToast(errMsg);
                        }
                    });
                }else if (tag == R.mipmap.video_fullscreen_normal) {
                    if (templateType == CCAtlasClient.TEMPLATE_SPEAK && !videoStreamView.getAudio() && !videoStreamView.getBlackStream()) {
                        ((LectureVideoManager) mVideoManager).videoFullScreen(mIconPopup.getPopupStreamPosition());
                    } else if (templateType == CCAtlasClient.TEMPLATE_SINGLE) {
                        ((MainVideoManager) mVideoManager).handleFullScreenVideo(videoStreamView, mIconPopup.getPopupStreamPosition());
                    } /*else if (mCurFragment instanceof TilingFragment) {
                    }*/
                } else if (tag == R.mipmap.kickout_normal) {
                    showLoading();
                    mCCBarLeyManager.kickUserFromSpeak(videoStreamView.getStream().getUserId(), new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissLoading();
                            showToast(err);
                        }
                    });
                } else if (tag == R.mipmap.send_cup_normal) {
                    mCCAtlasClient.ccSendCupData(videoStreamView.getStream().getUserId(),
                            videoStreamView.getStream().getUserName());
                }
            }
        });
    }

    /**
     * 需要考虑图片放大或者 其他视图全屏的情况
     */
    @Override
    public void onBackPressed() {
        if (menuMoreTeacherView != null && menuMoreTeacherView.isShow()) {
            menuMoreTeacherView.dismiss();
            return;
        }
        if (templateType == CCAtlasClient.TEMPLATE_SPEAK && mVideoManager != null) {
            if (mVideoManager instanceof LectureVideoManager) {
                LectureVideoManager lectureVideoManager = (LectureVideoManager) mVideoManager;
                if (lectureVideoManager.isVideoFullScreen()) {
                    lectureVideoManager.exitVideoFullScreen(true);
                    return;
                }
            }
        }
        if (ccDocView != null && ccDocView.isDocFullScreen()) {
            ccDocView.docExitFullScreen();
            return;
        }
        if (assistVideoView != null && assistVideoView.isFullScreen()) {
            assistVideoView.exitFullScreen();
            return;
        }
        if (shareVideoView != null && shareVideoView.isFullScreen()) {
            shareVideoView.exitFullScreen();
            return;
        }
        if (interCutVideoView != null && interCutVideoView.isRemoteVideoFullScreen()) {
            interCutVideoView.exitFullScreenVideo();
            return;
        }
        if (chatView != null && chatView.showBigPicture()) {
            chatView.hideBigPicture();
            return;
        }
        showCloseDialog();
    }

    @Override
    protected void exitRoom() {
        if(mExitAction == EXIT_ACTION_STOP){
            unpublish(false);
        }else{
            if (isPublish) {
                if (mCCAtlasClient.getInteractBean() != null &&
                        mCCAtlasClient.getInteractBean().getAssistantSwitch() == 1) {
                    if (mCCAtlasClient.getUserList() != null) {
                        for (CCUser user :mCCAtlasClient.getUserList()) {
                            if (user.getUserRole() == ASSISTANT && user
                                    .getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
                                leaveHome();
                                return;
                            }
                        }
                        unpublish(true);
                    }
                } else {
                    unpublish(true);
                }
            } else {
                leaveHome();
            }
        }
    }

    protected void leaveHome() {
        mCCAtlasClient.leave(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tools.log(TAG,"leave room onSuccess:");
                dismissLoading();
                releaseViewData();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.log(TAG,"leave room  onFailure ,errCode:"+errCode+",errMsg:"+errMsg);
                dismissLoading();
                Tools.showToast(errMsg);
            }
        });
    }

    @Override
    protected void releaseViewData() {
        super.releaseViewData();
        dismissLoading();
        dismissDialogs(cameraDialog, stopLiveDialog, startLiveDialog, endLastLiveDialog, recordDialog);

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

    public void toggleTopAndBottom() {
        if(sClassDirection!=1)return;
        if (menuTopView.isPerformingAnim()) {
            return;
        }
//        int currentType = mCCAtlasClient.getInteractBean().getTemplate();
        if(menuTopView.isShow()){
            menuTopView.startHideAnim();
            menuBottomView.startHideAnim();
            chatView.setVisibility(View.GONE);

        }else{
            menuTopView.startShowAnim();
            menuBottomView.startShowAnim();
            chatView.setVisibility(View.VISIBLE);

        }
    }
    @Override
    public void onVideoClick(int position, VideoStreamView videoStreamView) {

        if (videoStreamView.getStream().getUserRole() == ASSISTANT) {
            if (mAssistantIconPopup == null) {
                initAssistantIconPopup();
            }
            mAssistantIconPopup.show(mRoot, position, videoStreamView);
        } else {
            if (mIconPopup == null)
                initIconPopup();
            mIconPopup.show(mRoot, position, videoStreamView);
        }
    }
    private StudentRoomActivity.OnUserCupCountListener onUserCupCountListener;
    private void setOnUserCupCountListener(StudentRoomActivity.OnUserCupCountListener onUserCupCountListener){
        this.onUserCupCountListener = onUserCupCountListener;
    }
    interface OnUserCupCountListener{
        void getUserCupCount(ArrayList<CCUser> users);
    }
    public void upDataCup(){
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
                            if(onUserCupCountListener!=null){
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
