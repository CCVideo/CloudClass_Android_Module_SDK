package com.bokecc.room.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.view.adapter.StringSelectAdapter;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;
import com.bokecc.sskt.base.CCAtlasClient;
import com.example.ccchatlibrary.CCChatCallBack;
import com.example.ccchatlibrary.CCChatManager;

import java.util.ArrayList;


public class SetSettingActivity extends TitleActivity<SetSettingActivity.LianMaiViewHolder> {

    String mMediaTypeBoth;
    String mMediaTypeAudio;
    String mLianmaiTypeFree;
    String mLianmaiTypeNamed;
    String mLianmaiTypeAuto;

    public static final int SETTING_MODE_MEDIA = 0;
    public static final int SETTING_MODE_LIANMAI = 1;

    private int mSettingMode = 0;

    private static final String KEY_SELECT_POSITION = "select_position";
    private static final String KEY_SETTING_MODE = "setting_mode";

    private static Intent newIntent(Context context, int position, int mode) {
        Intent intent = new Intent(context, SetSettingActivity.class);
        intent.putExtra(KEY_SELECT_POSITION, position);
        intent.putExtra(KEY_SETTING_MODE, mode);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startSelf(Context context, int position, int mode) {
        context.startActivity(newIntent(context, position, mode));
    }

    View mRoot;
    private StringSelectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lian_mai_setting_ui;
    }

    @Override
    protected void beforeSetContentView() {
        if (CCRoomActivity.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mMediaTypeBoth =getString(R.string.setting_media_both);
        mMediaTypeAudio =getString(R.string.setting_media_audio);
        mLianmaiTypeFree =getString(R.string.setting_lianmai_free);
        mLianmaiTypeNamed =getString(R.string.setting_lianmai_named);
        mLianmaiTypeAuto =getString(R.string.setting_lianmai_auto);
    }

    @Override
    protected LianMaiViewHolder getViewHolder(View contentView) {
        return new LianMaiViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final LianMaiViewHolder holder) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).
                title("连麦模式").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        returnBack();
                    }
                }).
                build();
        setTitleOptions(options);

        final int selPosition = getIntent().getExtras().getInt("position");
        mSettingMode = getIntent().getExtras().getInt("mode");
        ArrayList<String> mDatas = new ArrayList<>();
        if (mSettingMode == SETTING_MODE_LIANMAI) {
            mDatas.add(mLianmaiTypeFree);
            mDatas.add(mLianmaiTypeNamed);
            mDatas.add(mLianmaiTypeAuto);
        } else {
            mDatas.add(mMediaTypeAudio);
            mDatas.add(mMediaTypeBoth);
        }
        holder.mLianmaiType.setLayoutManager(new LinearLayoutManager(this));
        holder.mLianmaiType.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN));
        mAdapter = new StringSelectAdapter(this);
        mAdapter.bindDatas(mDatas);
        mAdapter.setSelPosition(selPosition); // 设置默认选中
        holder.mLianmaiType.addOnItemTouchListener(new BaseOnItemTouch(holder.mLianmaiType,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        final int curPosition = holder.mLianmaiType.getChildAdapterPosition(viewHolder.itemView);
                        if (selPosition == curPosition) {
                            finish();
                            return;
                        }
                        Intent data = new Intent();
                        data.putExtra("mode_set",curPosition);
                        setResult(Activity.RESULT_OK, data);
                        showLoading();
                        if (mSettingMode == SETTING_MODE_MEDIA) {
                            CCChatManager.getInstance().setMediaMode(curPosition == 0 ? CCAtlasClient.MEDIA_MODE_AUDIO :
                                            CCAtlasClient.MEDIA_MODE_BOTH,
                                    new CCChatCallBack<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAdapter.setSelPosition(curPosition);
                                            dismissLoading();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 300L);
                                        }

                                        @Override
                                        public void onFailure(String err) {
                                            dismissLoading();
                                            Tools.showToast(err);
                                        }
                                    });
                        } else {
                            CCChatManager.getInstance().setLianmaiMode(curPosition == 0 ? CCAtlasClient.LIANMAI_MODE_FREE :
                                            curPosition == 1 ? CCAtlasClient.LIANMAI_MODE_NAMED : CCAtlasClient.LIANMAI_MODE_AUTO,
                                    new CCChatCallBack<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAdapter.setSelPosition(curPosition);
                                            dismissLoading();
                                            mTitleBar.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 300L);
                                        }

                                        @Override
                                        public void onFailure(String err) {
                                            dismissLoading();
                                            Tools.showToast(err);
                                        }
                                    });
                        }
                    }
                }));
        holder.mLianmaiType.setAdapter(mAdapter);

    }

    private void returnBack() {
        setResult(-1);
        exit();
    }

    private void exit() {
        setResult(Config.SELECT_RESULT_CODE);
        finish();
    }

    final class LianMaiViewHolder extends TitleActivity.ViewHolder {

        private RecyclerView mLianmaiType;

        LianMaiViewHolder(View view) {
            super(view);
            mLianmaiType = view.findViewById(R.id.id_lianmai_datas);
        }
    }

}
