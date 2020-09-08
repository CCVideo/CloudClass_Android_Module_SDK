package com.bokecc.room.ui.view.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.dialog.CustomTextViewDialog;
import com.bokecc.common.utils.NetWorkStateReceiver;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.manager.InteractSessionManager;
import com.bokecc.room.ui.utils.RequestUtil;
import com.bokecc.room.ui.view.chat.ChatView;
import com.bokecc.room.ui.view.chat.ChatViewListener;
import com.bokecc.room.ui.view.video.widget.InterCutVideoView;
import com.bokecc.room.ui.view.video.widget.WarmUpVideoView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.example.ccbarleylibrary.CCBarLeyManager;
import com.example.ccchatlibrary.CCChatManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 直播间基类
 * 功能点：
 * 1.网络监听
 * 2.音频/蓝牙的监听；
 * 3.sdk的视频，连麦，文档，聊天的管理
 * 4.sdk回调处理
 * @author wy
 */
public abstract class CCRoomActivity extends CCBaseActivity {

    /**网络监听类*/
    public NetWorkStateReceiver mNetWorkStateReceiver;
    /**底层管理类*/
    protected CCAtlasClient mCCAtlasClient;
    /**连麦管理者*/
    protected CCBarLeyManager mCCBarLeyManager;
    /**文档管理者*/
    protected CCDocViewManager mCCDocViewManager;
    /**聊天管理者*/
    protected CCChatManager mCCChatManager;

    /**横竖屏标志位*/
    public static int sClassDirection = 0;
    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    /**sdk接口*/
    protected InteractSessionManager mInteractSessionManager;
    protected EventBus mEventBus;


    /**是否被踢出房间*/
    protected boolean isKick = false;

    /**屏幕方向*/
    public static final String ScreenDirectionKey = "ScreenDirectionKey";
    /**暖场视频地址*/
    public static final String app_playurl = "app_playurl";

    private TimerTask mTimerTask;

    private AudioManager audioManager;
    /**暖场控件*/
    protected WarmUpVideoView warmUpVideoView;
    /**插播音视频控件*/
    protected InterCutVideoView interCutVideoView;
    private OrientationEventListener mOrientationListener;

    /**暖场视频地址*/
    protected String mAppPlayUrl;
    /**聊天控件*/
    protected ChatView chatView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SPUtil.getIntsance().put(Config.MIRRORING_MODE, 0);
        getBundle();
        initManager();
        setOrientation();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕不熄灭
        super.onCreate(savedInstanceState);

        registerNetworkStateReceiver();//注册网络监听
        initSdk();//初始化sdk
        initEventBus();//事件监听
//        initAudioBroadcastReceiver();
        loopUserCount();//轮询获取用户数量
    }

    /**
     * 获取数据
     */
    private void getBundle(){
        sClassDirection = getIntent().getIntExtra(ScreenDirectionKey,0);
        mAppPlayUrl = getIntent().getStringExtra(app_playurl);



    }

    /**
     * 初始化
     */
    private void initManager(){
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 设置屏幕方向
     */
    private void setOrientation(){

        if (sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //屏幕方向监听
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            private int lastModeInt;
            private int modeInt;

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation > 330 || orientation < 30) { //0度
                    orientation = 0;
                    modeInt = 0;

                } else if (orientation > 60 && orientation < 120) { //90度
                    orientation = 90;
                    modeInt = 3;
                } else if (orientation > 150 && orientation < 210) { //180度
                    orientation = 180;
                    modeInt = 2;
                } else if (orientation > 240 && orientation < 300) { //270度
                    orientation = 270;
                    modeInt = 1;
                } else {
                    modeInt = -1;
                }
                if (modeInt != -1 && lastModeInt != modeInt) {
                    lastModeInt = modeInt;
                    //修正远程流方向
                    mCCAtlasClient.setAppOrientation(lastModeInt);
                    //横屏状态下需要做进一步处理，如果某些机型不行请自行适配
                    if (sClassDirection == 1) {
                        if (orientation == 270)
                            orientation = 0;
                        else
                            orientation = orientation + 90;
                    }
                    //修正预览方向，不加的话远程流正常，本地和 UI 方向一致,根据需要选择是否开启
//                    mCCAtlasClient.setPreviewRotation(orientation);
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOrientationListener != null)
            mOrientationListener.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mOrientationListener != null)
            mOrientationListener.disable();
    }

    /**获取用户数量的定时器*/
    private Timer mUserCountTimer;
    /**
     * 定时获取房间人数
     */
    private void loopUserCount() {
        mUserCountTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mUserCountTimer == null) {
                    return;
                }
                if (!isKick) {
                    mCCAtlasClient.getUserCount();
                } else {
                    cancelUserCount();
                }
            }
        };
        mUserCountTimer.schedule(mTimerTask, 500, 3000);
    }

    private void cancelUserCount() {
        if (mUserCountTimer != null) {
            mUserCountTimer.cancel();
            mUserCountTimer = null;
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 初始化音频广播通知
     */
    private void initAudioBroadcastReceiver(){
//        mMyBroadcastReceiver = new CCBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
//        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
////            intentFilter.addAction("android.intent.action.PHONE_STATE");
//        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
//        registerReceiver(mMyBroadcastReceiver, intentFilter);
    }

    private void initSdk(){
        mCCAtlasClient = CCAtlasClient.getInstance();
        if (mCCAtlasClient != null){
            mCCAtlasClient.setTimeOut(5,5,5);
            mCCAtlasClient.setPlayViewModeFit(true);
        }
        mCCBarLeyManager = CCBarLeyManager.getInstance();
        mCCDocViewManager = CCDocViewManager.getInstance();
        mCCChatManager = CCChatManager.getInstance();
    }

    private void initEventBus(){
        mEventBus = EventBus.getDefault();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
        mInteractSessionManager = InteractSessionManager.getInstance();
        mInteractSessionManager.addInteractListeners();
        mInteractSessionManager.setEventBus(mEventBus);
    }

    /**
     * 这只聊天 View
     * @param chatViewId
     */
    protected void setChatView(int chatViewId) {
        //聊天发送
        chatView = findViewById(chatViewId);
        chatView.setClickable(false);//控件不可点击
        chatView.setListener(this,new ChatViewListener() {
            @Override
            public void sendMsg(String msg) {
                mCCChatManager.sendMsg(msg);
            }

            @Override
            public void sendPic(final File file) {
                RequestUtil.getUpdatePicToken(mCCAtlasClient,mCCChatManager,file);
            }
        });
    }
    /**
     *  设置聊天 View 的背景
     */
    protected void setChatViewBg() {
        if(chatView!=null)
        chatView.setChatViewNoneBg(!(sClassDirection == 1&&mCCAtlasClient.isRoomLive()));
    }

    /**
     * 系统界面的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == chatView.REQUEST_SYSTEM_PICTURE){
            if(chatView != null){
                chatView.handlePic(data);
            }
        }
    }


    /**
     * 处理音视频声音
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean warmUpVideo = warmUpVideoView!=null&&warmUpVideoView.isVideoPlay();
        boolean interCutVideo = interCutVideoView!=null&&(interCutVideoView.isVideoPlay()||interCutVideoView.isAudioPlay());
        Tools.log("onKeyDown","======warmUpVideo=="+warmUpVideo+"======interCutVideo=="+interCutVideo);
        if(warmUpVideo||interCutVideo){
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    return true;
                 default:
                   return  super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 注册网络监听
     */
    private void registerNetworkStateReceiver(){
        if (mNetWorkStateReceiver == null) {
            mNetWorkStateReceiver = new NetWorkStateReceiver();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkStateReceiver, filter);

        mNetWorkStateReceiver.setOnTimerConnectoListener(new NetWorkStateReceiver.OnTimerConnectoListener() {
            @Override
            public void onTimerDisConnected() {
                //断网9s退出
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showExitDialog("网络已断开，请检查网络设置！");
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitDialog("是否确认离开课堂？");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mNetWorkStateReceiver != null){
            unregisterReceiver(mNetWorkStateReceiver);
        }
        //注销EventBus
        if (mEventBus != null && mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
        cancelUserCount();

        com.tencent.mars.xlog.Log.appenderFlush(false);
    }

    /**
     * 退出提示
     * @param tip
     */
    protected void showExitDialog(String tip){
        CustomTextViewDialog dialog = new CustomTextViewDialog(this);
        dialog.setMessage(tip);
        dialog.setBtn( "取消", "确认",new CustomTextViewDialog.CustomClickListener() {
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



    /**
     * 退出提示（一个按钮）
     * @param tip
     */
    protected void showExitDialogOneBtn(String tip){
        CustomTextViewDialog dialog = new CustomTextViewDialog(this);
        dialog.setMessage(tip);
        dialog.setBtn("确认",new CustomTextViewDialog.CustomClickListener() {
            @Override
            public void onLeftClickListener() {

            }

            @Override
            public void onRightClickListener() {

            }

            @Override
            public void onOneBtnClickListener() {
                releaseViewData();
            }
        });
    }

    /**退出直播间，调用底层接口*/
    protected abstract void exitRoom();

    /**释放视图资源*/
    protected void releaseViewData(){
        cancelUserCount();
    }

    protected void dismissDialogs(Dialog ... dialogs){
        if(dialogs!=null&&dialogs.length>0){
            for (Dialog dialog : dialogs) {
                if(dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        }
    }

}
