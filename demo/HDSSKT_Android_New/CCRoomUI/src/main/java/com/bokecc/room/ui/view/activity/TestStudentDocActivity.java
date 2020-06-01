package com.bokecc.room.ui.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bokecc.common.utils.CheckNavBarUtil;
import com.bokecc.common.utils.DensityUtil;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.view.doc.CCDocView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.ccdocview.model.DocInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TestStudentDocActivity extends CCRoomActivity implements CCDocView.IDocHandleListener {

    private CCDocView ccDocView;
    private CCAtlasClient mCCAtlasClient;
    private static final String TAG = "TestStudentDocActivity";

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what){
            //开始上课
            case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START:
                if(ccDocView!=null){
                    ccDocView.restoreNormal();
                }

                break;
            //下课
            case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP:
                if(ccDocView!=null){
                    ccDocView.clearAll();
                }
                break;
            case Config.INTERACT_EVENT_WHAT_AUTH_DRAW:
                ccDocView.authDrawOrSetupTeacher((String) event.obj, (Boolean) event.obj2,2);
                break;
            //设为讲师
            case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER:
                ccDocView.authDrawOrSetupTeacher((String) event.obj, (Boolean) event.obj2, 4);
                break;
            case Config.INTERACT_EVENT_WHAT_SETUP_THEACHER_PAGE:
                ccDocView.setDocInfo((DocInfo) event.obj, (int) event.obj2, 0);
                break;
            case Config.INTERACT_EVENT_WHAT_DOC_CHANGE:
                ccDocView.setupTeacherFlag((DocInfo) event.obj, (int) event.obj2);
                break;

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_doc);
        int width = DensityUtil.getWidth(this);
        mCCAtlasClient = CCAtlasClient.getInstance();
        ccDocView = findViewById(R.id.ccDocView);
        int role = CCAtlasClient.TALKER;
        ccDocView.initRole(this,role,0);
        ccDocView.setDocHandleListener(this);



       /* mDocSmallRelativeLayout = new RelativeLayout(this);
        mDocSmallRelativeLayout.setGravity(Gravity.CENTER);
        mDocSmallRelativeLayout.setBackgroundColor(Color.WHITE);
        DocWebView mDocWebView = new DocWebView(this);
        mDocSmallRelativeLayout.addView(mDocWebView,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        DocView mDocView = new DocView(this, null);
        mDocSmallRelativeLayout.addView(mDocView,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //白板与 ppt 动画的交互
        mDocWebView.setDocSetVisibility(mDocView);
        mDocView.setDocWebViewSetVisibility(mDocWebView);
        mDocView.setWhiteboard(width,width*9/16,false);
        mDocView.setGestureAction(true);

        RelativeLayout mDocBigRelativeLayout = new RelativeLayout(this);
        mDocBigRelativeLayout.setGravity(Gravity.CENTER);
        mDocBigRelativeLayout.setClickable(true);
        mDocBigRelativeLayout.addView(mDocSmallRelativeLayout,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        GestureViewBinder mGuestureViewBinder = GestureViewBinder.bind(this, mDocBigRelativeLayout, mDocSmallRelativeLayout);
        mGuestureViewBinder.setFullGroup(true);
        mDocBigRelativeLayout.setBackgroundColor(Color.WHITE);*/

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_doc;
    }

    @Override
    public void onBackPressed() {
        if(ccDocView.isDocFullScreen()){
            ccDocView.docExitFullScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ccDocView.release();
        mCCAtlasClient.leave(null);
    }

    @Override
    protected void exitRoom() {
        finish();
    }

    @Override
    protected void releaseViewData() {

    }

    /**
     * 点击退出文档全屏按钮的回调
     */
    @Override
    public void exitDocFullScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    /**
     * 点击文档全屏按钮的回调
     */
    @Override
    public void docFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        CheckNavBarUtil.hideBottomUIMenu(this);
    }
    /**
     * 横屏状态点击显示上下菜单的方法
     */
    @Override
    public void showActionBar() {
    }
    /**
     * 竖屏非全屏状态点击文档区的回调方法
     */
    @Override
    public void onClickDocView(View view) {
    }

    /**
     * 竖屏非全屏状态点击文档区菜单出现动画开始
     */
    @Override
    public void onMenuShowAnimStart() {
    }
    /**
     * 竖屏非全屏状态点击文档区菜单隐藏动画开始
     */
    @Override
    public void onMenuHideAnimStart() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ccDocView.onActivityResult(requestCode,resultCode,data);

    }

}
