package com.bokecc.room.ui.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.common.utils.CheckNavBarUtil;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.doc.CCDocView;

public class TestTeacherDocActivity extends CCRoomActivity implements CCDocView.IDocHandleListener {


    private CCDocView ccDocView;
    private CCAtlasClient mCCAtlasClient;
    private boolean isDocFull;
    private static final String TAG = "TestTeacherDocActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCCAtlasClient = CCAtlasClient.getInstance();
        ccDocView = findViewById(R.id.ccDocView);
        int role = CCAtlasClient.PRESENTER;
        ccDocView.initRole(this,role,0);
        ccDocView.setDocHandleListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_teacher_doc;
    }

    /**
     * 点击退出文档全屏按钮的回调
     */
    @Override
    public void exitDocFullScreen() {
        isDocFull = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    /**
     * 点击文档全屏按钮的回调
     */
    @Override
    public void docFullScreen() {
        isDocFull = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        CheckNavBarUtil.hideBottomUIMenu(this);
    }
    /**
     * 横屏状态点击显示上下菜单的方法
     */
    @Override
    public void showActionBar() {
        Log.e(TAG,"======showActionBar========");
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
    @Override
    public void onBackPressed() {
        if(isDocFull){
            ccDocView.docExitFullScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCCAtlasClient.leave(null);
    }

    @Override
    protected void exitRoom() {

    }

    @Override
    protected void releaseViewData() {

    }
}
