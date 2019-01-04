package ccsskt.bokecc.base.example;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.kaola.qrcodescanner.qrcode.QrCodeActivity;
import ccsskt.bokecc.base.example.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 作者 ${CC视频}.<br/>
 */

@RuntimePermissions
public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.id_main_version)
    TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onViewCreated() {
        mVersion.setText(CCApplication.getVersion());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest
            .permission.READ_EXTERNAL_STORAGE})
    void goScan() {
        // NOTE: Perform action that requires the permission. If this is run by PermissionsDispatcher, the permission will have been granted
        go(QrCodeActivity.class);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest
            .permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(request);
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest
            .permission.READ_EXTERNAL_STORAGE})
    void onCameraNeverAskAgain() {
        Toast.makeText(this, "相机权限被拒绝，并且不会再次询问", Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("禁止", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("当前应用需要开启相机扫码和进行推流")
                .show();
    }

    @OnClick(R.id.id_home_scan)
    void scan() {
        HomeActivityPermissionsDispatcher.goScanWithCheck(this);
    }

    @OnClick(R.id.id_home_link)
    void goByLink() {
        go(LinkActivity.class);
    }

    @Override
    protected void protectApp() {
        go(SplashActivity.class);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String value = intent.getStringExtra(Config.FORCE_KILL_ACTION);
        if (!TextUtils.isEmpty(value) && value.equals(Config.FORCE_KILL_VALUE)) {
            protectApp();
        }
    }
}
