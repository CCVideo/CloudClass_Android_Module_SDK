package com.bokecc.room.ui.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bokecc.common.utils.TimeUtil;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.ItemLayout;
import com.bokecc.sskt.base.CCAtlasClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class NamedActivity extends TitleActivity<NamedActivity.NamedViewHolder> {

    private int mNamedContinueTime = 10;
    public static long mNamedTimeEnd = 0L;
    public static int mNamedTotalCount = 0;
    public static int mNamedCount = 0;
    private EventBus mEventBus;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_named_ui;
    }

    @Override
    protected NamedViewHolder getViewHolder(View contentView) {
        return new NamedViewHolder(contentView);
    }

    @Override
    protected void beforeSetContentView() {
        mEventBus = EventBus.getDefault();
        if (CCRoomActivity.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onBindViewHolder(NamedViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("点名").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        holder.mSetTime.setValue(TimeUtil.format(mNamedContinueTime));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    protected void onStop() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        mNamedContinueTime = data.getExtras().getInt("time");
        updateNamedContinueTime(mNamedContinueTime);
    }

    private void updateNamedContinueTime(int time) {
        mViewHolder.mSetTime.setValue(TimeUtil.format(time));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_ROLL_CALL_LIST:
                ArrayList<String> ids = (ArrayList<String>) event.obj2;
                if ((boolean) event.obj && ids != null && !ids.isEmpty()) {
                    mNamedCount = 0;
                    mNamedTotalCount = ids.size();
                    finish();
//                    NamedCountActivity.startActivity(NamedActivity.this, mNamedContinueTime);
                    NamedCountActivity.startSelf(NamedActivity.this, mNamedContinueTime);
                    return;
                }
                if (!(boolean) event.obj) {
                    Tools.showToast("发送点名失败");
                    return;
                }
                Tools.showToast("当前直播间没有学生");
        }
    }

    final class NamedViewHolder extends TitleActivity.ViewHolder {

        ItemLayout mSetTime;

        NamedViewHolder(View view) {
            super(view);
            mSetTime = view.findViewById(R.id.id_named_set_time);
            view.findViewById(R.id.id_named_set_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("current_continue_time", mNamedContinueTime);
                    go(NamedTimeActivity.class, Config.NAMED_REQUEST_CODE, bundle);
                }
            });
            view.findViewById(R.id.id_named_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CCAtlasClient.getInstance().startNamed(mNamedContinueTime);
                }
            });
        }

    }

}
