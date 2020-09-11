package com.bokecc.cloudclass.demo.activity;


import android.Manifest;
import android.content.Intent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.cloudclass.demo.Config;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.drag.model.MyEBEvent;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.exception.StreamException;
import com.bokecc.stream.bean.CCStream;
import com.example.ccbarleylibrary.CCBarLeyCallBack;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_AUTO;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_FREE;
import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_AUDIO;
import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_BOTH;

@RuntimePermissions
public class StudentDemoActivity extends StudentBaseActivity implements View.OnClickListener {

    private RelativeLayout rl_stream_t;
    private RelativeLayout rl_stream_s;
    private TextView tv_00;
    private TextView tv_01;
    private TextView tv_02;
    private TextView tv_03;
    private TextView tv_class_tip;
    //房间连麦模式
    private int lianMaiMode;
    /**
     * 默认状态
     */
    private final int MAI_STATUS_NORMAL = 0;
    /**
     * 等待排麦队列
     */
    private final int MAI_STATUS_QUEUE = 1;
    /**
     * 正在连麦
     */
    private final int MAI_STATUS_ING = 2;
    /**
     * 连麦成功
     */
    private final int MAI_STATUS_SUCCESS = 3;
    /***/
    private boolean haveDownMai = true;
    /**
     * 连麦状态
     */
    private int mMaiStatus = 0;

    private SurfaceView mLocalSurfaceView;
    private SurfaceView mRemoteSurfaceView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_demo;
    }

    @Override
    protected void onViewCreated() {
        rl_stream_t = findViewById(R.id.rl_stream_t);
        rl_stream_s = findViewById(R.id.rl_stream_s);
        tv_00 = findViewById(R.id.tv_00);
        tv_00.setOnClickListener(this);
        tv_01 = findViewById(R.id.tv_01);
        tv_01.setOnClickListener(this);
        tv_02 = findViewById(R.id.tv_02);
        tv_02.setOnClickListener(this);
        tv_03 = findViewById(R.id.tv_03);
        tv_03.setOnClickListener(this);
        tv_class_tip = findViewById(R.id.tv_class_tip);
    }

    /**
     * join成功后做的判断
     */
    @Override
    protected void onInitDate() {

        // 是否开播
        if (!mCCAtlasClient.isRoomLive()) {
            tv_class_tip.setText("未开启直播");
        } else {
            //隐藏未开始提示
            tv_class_tip.setText("开启直播");
            //开始进来首先获取流对象，直接去订阅
            mCCAtlasClient.setSubscribeRemoteStreams();
        }
        //连麦模式
        lianMaiMode = mCCAtlasClient.getInteractBean().getLianmaiMode();//只做自由连麦的demo ==

    }

    /**
     * 监听事件
     *
     * @param event
     */
    @Override
    protected void onInteractEvent(MyEBEvent event) {
        try {
            switch (event.what) {
                case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_START:
                    tv_class_tip.setText("直播进行中ing");
                    break;
                case Config.INTERACT_EVENT_WHAT_CLASS_STATUS_STOP:
                    tv_class_tip.setText("未开启直播");
                    break;
                case Config.INTERACT_EVENT_WHAT_UP_MAI:
                    publish();
                    break;
                case Config.INTERACT_EVENT_WHAT_DOWN_MAI:
                    unpublish();
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_ADDED:
                    //订阅流
                    final SubscribeRemoteStream stream = (SubscribeRemoteStream) event.obj;
                    subscibeStream(stream);
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_REMOVED:
                    //移除流
                    final SubscribeRemoteStream unStream = (SubscribeRemoteStream) event.obj;
                    unSubscibeStream(unStream);
                    break;
                case Config.INTERACT_EVENT_WHAT_STREAM_ERROR:
                    //流错误
                    break;
                case Config.INTERACT_EVENT_WHAT_SERVER_DISCONNECT:
                    //服务断开链接
                    break;
                case Config.INTERACT_EVENT_WHAT_SERVER_CONNECT:
                    //服务链接
                    break;
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_00) {
            exitRoom();
        } else if (v.getId() == R.id.tv_01) {
            if (lianMaiMode != LIANMAI_MODE_FREE) {
                Tools.showToast("请将房间连麦模式调整为自由连麦");
                return;
            }
            lianmai();
        } else if (v.getId() == R.id.tv_02) {
            handDownMai();
        } else if (v.getId() == R.id.tv_03) {
            startActivity(new Intent(this,DocDemoActivity.class));
        }
    }

    /**
     * 连麦
     */
    private void lianmai() {
        try {
            if (mMaiStatus == MAI_STATUS_NORMAL) {
                StudentDemoActivityPermissionsDispatcher.doRequestMaiWithPermissionCheck(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 权限回调
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest
            .permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void doRequestMai() {
        startLianmai();
    }

    /**
     * 开始连麦
     */
    private void startLianmai() {
        mCCBarLeyManager.handsup(new CCBarLeyCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mMaiStatus = MAI_STATUS_QUEUE;
            }

            @Override
            public void onFailure(String err) {
                Tools.showToast(err);
            }
        });
    }

    /**
     * 下麦
     */
    private void handDownMai() {
        if (mMaiStatus != CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
            Tools.showToast("您未推流，无需下麦");
            return;
        }
        mCCBarLeyManager.handsDown(new CCBarLeyCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(String err) {
                Tools.showToast(err);
            }
        });
    }

    /**
     * 推流
     */
    private synchronized void publish() {
        try {
            if (mCCAtlasClient.getInteractBean() == null) {
                return;
            }
            //设置摄像头和音频的开启权限
            if (mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
                mCCAtlasClient.getInteractBean().getUserSetting().setAllowAudio(true);
                mCCAtlasClient.getInteractBean().getUserSetting().setAllowVideo(true);
            }

            //获取预览视图
            mLocalSurfaceView = mCCAtlasClient.startPreview(this, com.bokecc.common.stream.config.Config.RENDER_MODE_HIDDEN);
            rl_stream_s.addView(mLocalSurfaceView);
            //开启推流
            mCCAtlasClient.publish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mMaiStatus = MAI_STATUS_SUCCESS;

                    if (mCCAtlasClient.getInteractBean().getMediaMode() == MEDIA_MODE_AUDIO) {
                        mCCAtlasClient.disableVideo(false);
                    } else if (mCCAtlasClient.getInteractBean().getMediaMode() == MEDIA_MODE_BOTH) {
                        mCCAtlasClient.enableVideo(true);
                    }
                    if (!mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
                        mCCAtlasClient.disableAudio(true);
                    }
                }

                @Override
                public void onFailure(int errCode, final String errMsg) {
                    Tools.showToast("推流失败：" + errMsg);
                    rl_stream_s.removeView(mLocalSurfaceView);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Tools.showToast("推流报错：" + e.getMessage());
            rl_stream_s.removeView(mLocalSurfaceView);
        }
    }

    /**
     * 结束推流
     */
    private synchronized void unpublish() {
        mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                rl_stream_s.removeView(mLocalSurfaceView);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.showToast(errMsg);
            }
        });
    }

    /**
     * 订阅流
     * @param stream
     */
    private synchronized void subscibeStream(SubscribeRemoteStream stream) {
        try {
            mCCAtlasClient.SubscribeStream(stream.getRemoteStream(), com.bokecc.common.stream.config.Config.RENDER_MODE_FIT, new CCAtlasCallBack<CCStream>() {
                @Override
                public void onSuccess(final CCStream ccstream) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl_stream_t.addView(ccstream.getSurfaceView());
                        }
                    });
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    Tools.showToast(errMsg);
                }
            });
        } catch (StreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止订阅
     * @param stream
     */
    private synchronized void unSubscibeStream(final SubscribeRemoteStream stream)  {
        try {
            mCCAtlasClient.unSubscribeStream(stream.getRemoteStream(), new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    rl_stream_t.removeView(stream.getRemoteStream().getSurfaceView());
                }

                @Override
                public void onFailure(int errCode, String errMsg) {

                }
            });
        } catch (StreamException e) {
            e.printStackTrace();
        }
    }
    /**
     * 退出房间
     */
    protected void exitRoom() {
        showProgress();
        if (mMaiStatus == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
            //如果有推流先停止推流，更新麦序，最后离开房间
            mCCAtlasClient.unpublish(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mCCBarLeyManager.handsDown(new CCBarLeyCallBack<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismissProgress();
                            mCCAtlasClient.leave(new CCAtlasCallBack<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dismissProgress();
                                    finish();
                                }

                                @Override
                                public void onFailure(int errCode, String errMsg) {
                                    dismissProgress();
                                    Tools.showToast(errMsg);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String err) {
                            dismissProgress();
                            Tools.showToast(err);
                        }
                    });
                    //移除流视图
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    Tools.showToast(errMsg);
                    //移除流视图
                }
            });
        } else {
            mCCAtlasClient.leave(new CCAtlasCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissProgress();
                    finish();
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    dismissProgress();
                    Tools.showToast(errMsg);
                }
            });
        }

    }
}
