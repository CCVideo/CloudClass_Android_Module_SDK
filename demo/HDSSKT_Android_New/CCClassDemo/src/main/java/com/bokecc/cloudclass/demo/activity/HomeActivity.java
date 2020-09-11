package com.bokecc.cloudclass.demo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.base.BaseActivity;
import com.bokecc.cloudclass.demo.util.ParseMsgUtil;
import com.bokecc.cloudclass.demo.view.ClearEditLayout;
import com.bokecc.sskt.base.common.dialog.CustomTextViewDialog;
import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.bean.UpdateInfo;
import com.bokecc.sskt.base.common.network.CCRequestCallback;
import com.bokecc.sskt.base.common.network.UpdateRequest;
import com.kaola.qrcodescanner.qrcode.QrCodeActivity;

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

    TextView mVersion;
    CheckBox mZbar;
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

        update();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onViewCreated() {
        mVersion = findViewById(R.id.id_main_version);
        mZbar = findViewById(R.id.id_zbar);
        mClearEditLayout = findViewById(R.id.id_link_url);

        findViewById(R.id.id_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        findViewById(R.id.operation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOperation();
            }
        });

        findViewById(R.id.id_link_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goByLink();
            }
        });

//        Intent intent = getIntent();
//        String url1 = intent.getDataString();
//        Uri uri = intent.getData();
        mVersion.setText("v" + Tools.getVersionName());
//        mVersion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgress();
//                CCAtlasClient.getInstance().reportLogInfo("Test",Tools.getPhoneModel()+"_"+Tools.getAndroidID(), new CCLogRequestCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        Tools.showToast("上传成功！"+result,true);
//                        dismissProgress();
//                    }
//
//                    @Override
//                    public void onFailure(int errCode, String errMsg) {
//                        Tools.showToast(errMsg);
//                        dismissProgress();
//                    }
//                });
//            }
//        });
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

    void scan() {
        HomeActivityPermissionsDispatcher.goScanWithPermissionCheck(this);
    }

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

    private void update(){
        new UpdateRequest(this, new CCRequestCallback<UpdateInfo>() {
            @Override
            public void onSuccess(UpdateInfo response) {
                if(response != null){
                    updateDialog(response);
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {
                Tools.log(errorMsg);
            }
        });
    }

    /**
     * 更新对话框
     * @param response
     */
    private void updateDialog(final UpdateInfo response){
        CustomTextViewDialog dialog = new CustomTextViewDialog(this);
        dialog.setTitle("软件更新");
        dialog.setMessage(response.getUpdate_infos());
        if(response.isIs_force()){
            dialog.setBtn("确认",new CustomTextViewDialog.CustomClickListener() {
                @Override
                public void onLeftClickListener() {

                }

                @Override
                public void onRightClickListener() {

                }

                @Override
                public void onOneBtnClickListener() {
                    Tools.openSystemBrowser(HomeActivity.this,response.getUrl());
                    finish();
                }
            });
        }else{
            dialog.setBtn( "取消", "确认",new CustomTextViewDialog.CustomClickListener() {
                @Override
                public void onLeftClickListener() {

                }

                @Override
                public void onRightClickListener() {
                    Tools.openSystemBrowser(HomeActivity.this,response.getUrl());
                }

                @Override
                public void onOneBtnClickListener() {

                }
            });
        }
    }


}
