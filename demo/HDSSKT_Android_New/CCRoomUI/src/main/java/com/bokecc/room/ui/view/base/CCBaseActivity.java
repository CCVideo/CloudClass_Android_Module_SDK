package com.bokecc.room.ui.view.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bokecc.common.dialog.CustomProgressDialog;
import com.bokecc.common.utils.Tools;

/**
 * Activity基类
 * Created by wy on 2019/1/4.
 */
public abstract class CCBaseActivity extends AppCompatActivity {

//    protected boolean isHideStatusbar = false;
    protected CCBaseActivity mContext;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Tools.log(getLocalClassName(),"onCreate");
        beforeSetContentView();
        setContentView(getLayoutId());
        //沉浸式标题栏，android 5.0以后支持
        /*if (Build.VERSION.SDK_INT >= 21 && isHideStatusbar) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            //如果使用沉浸式标题栏，会使adjustResize模式失效，加入以下代码可以修复
            AndroidBug5497Workaround.assistActivity(this);
        }*/
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }



    protected void beforeSetContentView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.log(getLocalClassName(),"onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tools.log(getLocalClassName(),"onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Tools.log(getLocalClassName(),"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Tools.log(getLocalClassName(),"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.log(getLocalClassName(),"onDestroy");
    }

    /**获取布局文件id*/
    protected abstract int getLayoutId();

    /**
     * 显示加载
     */
    protected void showLoading(){
        startProgress(Tools.getString(com.bokecc.common.R.string.load_tip));
    }

    /**
     * 显示加载
     */
    protected void showLoading(int id){
        startProgress(Tools.getString(id));
    }

    /**
     * 隐藏加载
     */
    protected void dismissLoading(){
        if(pd != null && pd.isShowing()){
            pd.dismiss();
            pd = null;
        }
    }

    protected void showProgress() {
        if(mProgressDialog==null){
            mProgressDialog = new Dialog(this, com.bokecc.common.R.style.ProgressDialog);
            mProgressDialog.setContentView(com.bokecc.common.R.layout.progress_layout);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        if (mProgressDialog!=null&&mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private CustomProgressDialog pd = null;
    private void startProgress(String msg){
        if(pd == null){
            pd = new CustomProgressDialog(this, msg,true,null);
        }
    }
    protected void go(Class clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }
    protected void go(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    protected void go(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }
}
