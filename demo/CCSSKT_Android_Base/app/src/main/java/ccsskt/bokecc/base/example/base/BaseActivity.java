package ccsskt.bokecc.base.example.base;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.MyBroadcastReceiver;
import com.bokecc.sskt.base.bean.CCInteractBean;

import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ccsskt.bokecc.base.example.CCApplication;
import ccsskt.bokecc.base.example.Config;
import ccsskt.bokecc.base.example.HomeActivity;
import ccsskt.bokecc.base.example.R;

/**
 * 作者 ${CC视频}.<br/>
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected View mRoot;

    protected Handler mHandler;
    private Unbinder mUnbinder;

    private Dialog mProgressDialog;
    public CCAtlasClient ccAtlasClient;
    protected CCInteractBean mCCInteractBean;
    protected MyBroadcastReceiver mMyBroadcastReceiver;
    private AudioManager audioManager;
//    private AppRTCAudioManager audioManager = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CCApplication.mAppStatus = 0;
        if (CCApplication.mAppStatus == -1) { // 如果被强杀不执行初始化操作
            protectApp();
        } else {
            beforeSetContentView();
            setContentView(getLayoutId());
            mUnbinder = ButterKnife.bind(this);
            mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
            mHandler = new Handler();
            ccAtlasClient = CCAtlasClient.getInstance();
            initProgressDialog();
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            mMyBroadcastReceiver = MyBroadcastReceiver.getInstance();
//            mMyBroadcastReceiver.initial(this,audioManager);
////            mMyBroadcastReceiver.initial(audioManager);
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//            intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
//            intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//            intentFilter.addAction("android.intent.action.PHONE_STATE");
//            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
//            registerReceiver(mMyBroadcastReceiver, intentFilter);
            onViewCreated();
        }
        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.

    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if(ccAtlasClient != null){
            ccAtlasClient.recycleClass();
        }
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
     * 应用被强杀 重启APP
     */
    protected void protectApp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Config.FORCE_KILL_ACTION, Config.FORCE_KILL_VALUE);
        startActivity(intent);
    }

    /**
     * 进行吐司提示
     *
     * @param msg 提示内容
     */
    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(TAG, "【 " + msg + " 】");
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
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void go(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void go(Class clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    protected void go(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }

}
