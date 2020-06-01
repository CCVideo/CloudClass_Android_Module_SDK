package com.bokecc.ccsskt.example.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.BaseActivity;
import com.bokecc.ccsskt.example.util.ParseMsgUtil;
import com.bokecc.ccsskt.example.view.ClearEditLayout;
import com.bokecc.common.log.CCLogRequestCallback;
import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.CCAtlasClient;
import com.kaola.qrcodescanner.qrcode.QrCodeActivity;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 主界面
 */
@RuntimePermissions
public class HomeActivity extends BaseActivity {


    @BindView(R.id.id_main_version)
    TextView mVersion;
    @BindView(R.id.id_zbar)
    CheckBox mZbar;
    @BindView(R.id.id_link_url)
    ClearEditLayout mClearEditLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            String scheme = intent.getScheme();
            if (scheme != null && scheme.equals("csslcloud")) {
                String url = intent.getDataString();
                parseH5Url(url);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onViewCreated() {
//        Intent intent = getIntent();
//        String url1 = intent.getDataString();
//        Uri uri = intent.getData();
        mVersion.setText("v" + Tools.getVersionName());
        mVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                CCAtlasClient.getInstance().reportLogInfo("Test",Tools.getPhoneModel()+"_"+Tools.getAndroidID(), new CCLogRequestCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Tools.showToast("上传成功！"+result,true);
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        Tools.showToast(errMsg);
                        dismissProgress();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void goScan() {
        // NOTE: Perform action that requires the permission. If this is run by PermissionsDispatcher, the permission will have been granted
        go(QrCodeActivity.class);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(request);
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
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

    @OnClick(R.id.id_scan)
    void scan() {
        HomeActivityPermissionsDispatcher.goScanWithPermissionCheck(this);
    }

    @OnClick(R.id.operation)
    void goOperation() {
        go(OperationActivity.class);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void goByLink() {
        String url = mClearEditLayout.getText();
        if (TextUtils.isEmpty(url)) {
            showToast("请输入链接");
            return;
        }
        parseUrl(url);
    }

    @OnClick(R.id.id_link_go)
    void goByClass() {
        goByLink();
    }

    private void parseUrl(String url) {
        ParseMsgUtil.parseUrl(HomeActivity.this, url, new ParseMsgUtil.ParseCallBack() {
            @Override
            public void onStart() {
                showProgress();
            }

            @Override
            public void onSuccess() {
                dismissProgress();
            }

            @Override
            public void onFailure(String err) {
                toastOnUiThread(err);
                dismissProgress();
            }
        });
    }

    private void parseH5Url(String url) {
        ParseMsgUtil.parseH5Url(HomeActivity.this, url, new ParseMsgUtil.ParseCallBack() {
            @Override
            public void onStart() {
                showProgress();
            }

            @Override
            public void onSuccess() {
                dismissProgress();
            }

            @Override
            public void onFailure(String err) {
                toastOnUiThread(err);
                dismissProgress();
            }
        });
    }


}
