package com.bokecc.cloudclass.demo.activity;

import android.Manifest;
import android.os.Handler;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.base.BaseActivity;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.callback.OnNotifyStreamListener;
import com.bokecc.stream.bean.CCStreamQuality;
import com.example.ccbarleylibrary.CCBarLeyManager;
import com.example.ccchatlibrary.CCChatManager;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.bokecc.common.utils.Tools.showToast;

@RuntimePermissions
public class RoomActivity extends BaseActivity {

    /**底层管理类*/
    protected CCAtlasClient mCCAtlasClient;
    /**连麦管理者*/
    protected CCBarLeyManager mCCBarLeyManager;
    /**文档管理者*/
    protected CCDocViewManager mCCDocViewManager;
    /**聊天管理者*/
    protected CCChatManager mCCChatManager;

    private RelativeLayout id_main_video_container;


    @Override
    protected int getLayoutId() {
        return R.layout.room_layout;
    }

    @Override
    protected void onViewCreated() {
        initSdk();
        initRoomListener();

        id_main_video_container = findViewById(R.id.id_main_video_container);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RoomActivityPermissionsDispatcher.startPreviewWithPermissionCheck(RoomActivity.this);
            }
        }, 1000);
    }

    /**
     * 初始化sdk
     */
    private void initSdk(){
        mCCAtlasClient = CCAtlasClient.getInstance();
        mCCBarLeyManager = CCBarLeyManager.getInstance();
        mCCDocViewManager = CCDocViewManager.getInstance();
        mCCChatManager = CCChatManager.getInstance();
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
//            mCCAtlasClient.setAppOrientation(true);//true横屏竖屏
//            //设置分辨率
//            mCCAtlasClient.setResolution(240);
            //开启预览
            SurfaceView surfaceView = mCCAtlasClient.startPreview(this,com.bokecc.common.stream.config.Config.RENDER_MODE_FIT);
            id_main_video_container.addView(surfaceView);

            startLive(0);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    /**
     * 开始直播
     */
    private void startLive(int isRecord) {
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
        mCCAtlasClient.publish(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("推流成功！");
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                showToast(errMsg);
            }
        });
    }

    /**
     * 初始化房间监听事件
     */
    private void initRoomListener(){
        mCCAtlasClient.setOnNotifyStreamListener(mNotifyStreamListener);
    }

    /**
     * 流变化通知
     */
    private OnNotifyStreamListener mNotifyStreamListener = new OnNotifyStreamListener() {

        @Override
        public void onStreamAllowSub(SubscribeRemoteStream remoteStream) {//流订阅回调

        }

        @Override
        public void onStreamRemoved(SubscribeRemoteStream remoteStream) {//移除流回调

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

        }

        @Override
        public void onPlayQuality(String uid, CCStreamQuality publishQuality) {

        }

    };

}
