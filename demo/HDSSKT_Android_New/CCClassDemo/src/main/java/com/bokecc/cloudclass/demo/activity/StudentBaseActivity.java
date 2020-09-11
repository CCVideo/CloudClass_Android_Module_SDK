package com.bokecc.cloudclass.demo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bokecc.cloudclass.demo.Config;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.drag.model.MyEBEvent;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.callback.OnClassStatusListener;
import com.bokecc.sskt.base.callback.OnNotifyStreamListener;
import com.bokecc.sskt.base.callback.OnServerListener;
import com.bokecc.stream.bean.CCStreamQuality;
import com.example.ccbarleylibrary.CCBarLeyManager;

/**
 * 作者 ${CC视频}.<br/>
 */
public abstract class StudentBaseActivity extends AppCompatActivity {
    /**底层管理类*/
    protected CCAtlasClient mCCAtlasClient;
    /**连麦管理者*/
    protected CCBarLeyManager mCCBarLeyManager;

    protected View mRoot;

    private Dialog mProgressDialog;

    public boolean isGo = false;

    public boolean isToast = true;

    /**用户sessionid*/
    public static final String SeesionidKey = "SeesionidKey";
    /**用户账号*/
    public static final String UserAccountKey = "UserAccountKey";
    /**所选区域*/
    public static final String AreaCodeKey = "AreaCodeKey";

    private String mSeesionid;
    private String mUserAccount;
    private String mAreaCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
        beforeSetContentView();
        setContentView(getLayoutId());
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
        initProgressDialog();
        initSdk();//初始化sdk
        initRoomListener();//初始化监听
        onViewCreated();
        join();//加入房间

    }

    @Override
    protected void onResume() {
        super.onResume();
        isGo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 在SetContentView之前进行操作，父类空实现，子类根据需要进行实现
     */
    protected void beforeSetContentView() {}

    /**获取布局id*/
    protected abstract int getLayoutId();

    /**界面创建完成*/
    protected abstract void onViewCreated();
    /**创建初始化数据*/
    protected void onInitDate(){};
    /** 监听事件处理 **/
    protected void onInteractEvent(MyEBEvent event){

    };
    /**
     * 获取数据
     */
    private void getBundle(){
        mSeesionid = getIntent().getStringExtra(SeesionidKey);
        mUserAccount = getIntent().getStringExtra(UserAccountKey);
        mAreaCode = getIntent().getStringExtra(AreaCodeKey);
    }
    /**
     * 初始化加载框
     */
    private void initProgressDialog() {
        mProgressDialog = new Dialog(this, R.style.ProgressDialog);
        mProgressDialog.setContentView(R.layout.progress_layout);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 显示加载
     */
    protected void showProgress() {
        if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }

    /**
     * 隐藏加载
     */
    protected void dismissProgress() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    /**
     * 进行吐司提示
     *
     * @param msg 提示内容
     */
    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg) || !isToast) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastOnUiThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
    }

    protected void go(Class clazz) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    /**
     * 事件统一处理方法
     */
    private void handleEvent(final MyEBEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onInteractEvent(event);
            }
        });
    }

    /**
     * 初始化sdk
     */
    private void initSdk(){
        mCCAtlasClient = CCAtlasClient.getInstance();
        mCCBarLeyManager = CCBarLeyManager.getInstance();
    }
    /**
     * 初始化房间监听事件
     */
    private void initRoomListener(){
        mCCAtlasClient.setOnServerListener(mServerDisconnectListener);
        mCCAtlasClient.setOnNotifyStreamListener(mNotifyStreamListener);
        mCCAtlasClient.setOnClassStatusListener(mClassStatusListener);

        mCCBarLeyManager.setOnNotifyMaiStatusLisnter(mNotifyMaiStatusLisnter);

    }
    /**
     * 监听直播间消息通道事件
     */
    protected OnServerListener mServerDisconnectListener = new OnServerListener() {
        @Override
        public void onConnect() {
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_CONNECT));
        }

        @Override
        public void onReconnect() {

        }

        @Override
        public void onReconnecting() {

        }

        @Override
        public void onDisconnect(int platform) {//直播间断开

        }

        @Override
        public void onReconnectFailed() {
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT));
        }

        @Override
        public void onDisconnect() {

        }
    };

    /**
     * 学生排麦状态监听事件
     */
    protected CCBarLeyManager.OnNotifyMaiStatusLisnter mNotifyMaiStatusLisnter = new CCBarLeyManager.OnNotifyMaiStatusLisnter() {
        @Override
        public void onUpMai(int oldStatus) {//上麦事件监听
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_UP_MAI, oldStatus));
        }

        @Override
        public void onDownMai() {//下麦事件监听
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_DOWN_MAI));
        }
    };

    /**
     * 流变化通知
     */
    protected OnNotifyStreamListener mNotifyStreamListener = new OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(SubscribeRemoteStream remoteStream) {
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ADDED, remoteStream));
        }

        @Override
        public void onStreamRemoved(SubscribeRemoteStream remoteStream) {//移除流回调
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_REMOVED, remoteStream));
        }

        @Override
        public void onServerInitSuccess() {

        }

        @Override
        public void onServerInitFail() {

        }

        @Override
        public void onServerConnected() {

        }

        @Override
        public void onServerReconnect() {

        }

        @Override
        public void onStreamError() {//流发生错误
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_ERROR));
        }

        @Override
        public void onServerDisconnected() {

        }

        @Override
        public void onStartRouteOptimization() {

        }

        @Override
        public void onStopRouteOptimization() {

        }

        @Override
        public void onRouteOptimizationError(String msg) {

        }

        @Override
        public void onReloadPreview() {

        }

        @Override
        public void onStudentDownMai() {
        }

        @Override
        public void onPublishQuality(CCStreamQuality publishQuality) {
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_STREAM_STATUS,publishQuality.getTxQuality(),publishQuality.getRtt(),publishQuality.getPktLostRate()));
        }

        @Override
        public void onPlayQuality(String uid, CCStreamQuality publishQuality) {

        }

    };

    /**
     * 上课状态变化通知
     * <p>
     */
    protected OnClassStatusListener mClassStatusListener = new OnClassStatusListener() {
        @Override
        public void onStart() {//开始上课事件通知
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START));
        }

        @Override
        public void onStop() {//下课事件通知
            handleEvent(new MyEBEvent(Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP));
        }
    };

    /**
     * 加入房间
     */
    private void join(){
        showProgress();
        CCAtlasClient.getInstance().join(mSeesionid, mUserAccount, mAreaCode, false, new CCAtlasCallBack<CCInteractBean>() {
            @Override
            public void onSuccess(CCInteractBean ccInteractBean) {
                onInitDate();
                dismissProgress();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissProgress();
                Tools.showToast(errMsg);
            }
        });
    }
}
