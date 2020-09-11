package com.bokecc.cloudclass.demo.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bokecc.cloudclass.demo.R;

/**
 * 作者 ${CC视频}.<br/>
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected View mRoot;

    private Dialog mProgressDialog;

    public boolean isGo = false;

    public boolean isToast = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(getLayoutId());
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);

        initProgressDialog();
        onViewCreated();
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

}
