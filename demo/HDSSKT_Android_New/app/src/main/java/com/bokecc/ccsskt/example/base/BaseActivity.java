package com.bokecc.ccsskt.example.base;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.util.NetWorkStateReceiver;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者 ${CC视频}.<br/>
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected View mRoot;

    protected Handler mHandler;
    private Unbinder mUnbinder;

    private Dialog mProgressDialog;

    protected boolean isStop = false;
    protected boolean isKick = false;

    protected boolean isPause = false;
    public boolean isGo = false;

    public boolean isToast = true;
    public NetWorkStateReceiver mNetWorkStateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(getLayoutId());
        isKick = false;
        mUnbinder = ButterKnife.bind(this);
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
        mHandler = new Handler();

        if (mNetWorkStateReceiver == null) {
            mNetWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkStateReceiver, filter);




        initProgressDialog();
        onViewCreated();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStop = false;
        isPause = false;
        isGo = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        unregisterReceiver(mNetWorkStateReceiver);

        super.onDestroy();
    }

    /**
     * 在SetContentView之前进行操作，父类空实现，子类根据需要进行实现
     */
    protected void beforeSetContentView() {
    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

    /**
     * 界面创建完成
     */
    protected abstract void onViewCreated();

    private void initProgressDialog() {
        mProgressDialog = new Dialog(this, R.style.ProgressDialog);
        mProgressDialog.setContentView(R.layout.progress_layout);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    protected void showProgress() {
        if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }


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

    protected void go(Class clazz, Bundle bundle) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void go(Class clazz, int requestCode) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    protected void go(Class clazz, int requestCode, Bundle bundle) {
        isGo = true;
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }


}
