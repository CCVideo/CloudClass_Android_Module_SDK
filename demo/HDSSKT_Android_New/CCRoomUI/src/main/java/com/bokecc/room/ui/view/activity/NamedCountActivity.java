package com.bokecc.room.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.common.utils.TimeUtil;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.CirclePercentView;
import com.bokecc.sskt.base.common.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bokecc.room.ui.view.activity.NamedActivity.mNamedCount;


public class NamedCountActivity extends TitleActivity<NamedCountActivity.NamedCountViewHolder> {

    private static final String KEY_NAMED_CONTINUE_TIME = "key_named_continue_time";
    private static final String NAMED_ANSWER_TIP = "参与点名的有";
    private static final String NAMED_ONLINE_TIP_P = "共有";
    private static final String NAMED_ONLINE_TIP_N = "在线";
    private EventBus mEventBus;
    private static final String TAG = "NamedCountActivity";

    private static Intent newIntent(Context context, int time) {
        Intent intent = new Intent(context, NamedCountActivity.class);
        intent.putExtra(KEY_NAMED_CONTINUE_TIME, time);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startSelf(Context context, int time) {
        context.startActivity(newIntent(context, time));
    }

    private boolean flag;

    private Runnable mCountTask = new Runnable() {
        @Override
        public void run() {
            if (!flag) {
                return;
            }
            mSeconds -= 1;
            if (mSeconds <= 0) {
                stopCountDown(false);
                return;
            }
            mViewHolder.mRestartNamed.setText(TimeUtil.formatNamed(mSeconds));
            mTitleBar.postDelayed(this, 1000);
        }
    };

    private int mSeconds;
    private String mNamedTip, mOnlineTip;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_named_count_ui;
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
    protected NamedCountViewHolder getViewHolder(View contentView) {
        return new NamedCountViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(NamedCountViewHolder holder) {
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

        mSeconds = getIntent().getExtras().getInt(KEY_NAMED_CONTINUE_TIME);
        mNamedTip = NAMED_ANSWER_TIP + mNamedCount + "名学生";
        holder.mNamedUsers.setText(textDiscolor(mNamedTip, NAMED_ANSWER_TIP.length(), mNamedTip.length()));
        mOnlineTip = NAMED_ONLINE_TIP_P + NamedActivity.mNamedTotalCount + "名学生" + NAMED_ONLINE_TIP_N;
        int end = (NAMED_ONLINE_TIP_P + NamedActivity.mNamedTotalCount + "名学生").length();
        mViewHolder.mOnlineUsers.setText(textDiscolor(mOnlineTip, NAMED_ONLINE_TIP_P.length(), end));
        if (mSeconds > 0) {
            holder.mNamedPercent.setMax(NamedActivity.mNamedTotalCount);
            holder.mNamedPercent.setProgress(mNamedCount);
            holder.mRestartNamed.setEnabled(false);
            holder.mRestartNamed.setBackgroundResource(R.drawable.disable_btn);
            holder.mRestartNamed.setText(TimeUtil.formatNamed(mSeconds));
            startCountDown();
        } else {
            holder.mNamedPercent.setMax(NamedActivity.mNamedTotalCount);
            holder.mNamedPercent.setProgress(mNamedCount);
            mViewHolder.mRestartNamed.setEnabled(true);
            mViewHolder.mRestartNamed.setBackgroundResource(R.drawable.round_btn);
            mViewHolder.mRestartNamed.setText(getResources().getString(R.string.restart_named));
        }
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
        stopCountDown(true);
    }

    private SpannableString textDiscolor(String source, int start, int end) {
        SpannableString spannableString = new SpannableString(source);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        flag = true;
        NamedActivity.mNamedTimeEnd = System.currentTimeMillis() + mSeconds * 1000; // 记录点名结束时间
        mTitleBar.postDelayed(mCountTask, 1000);
    }

    /**
     * 停止计数
     */
    private void stopCountDown(boolean isPause) {
        flag = false;
        mTitleBar.removeCallbacks(mCountTask);
        if (!isPause) {
            mViewHolder.mRestartNamed.setEnabled(true);
            mViewHolder.mRestartNamed.setBackgroundResource(R.drawable.round_btn);
            mViewHolder.mRestartNamed.setText(getResources().getString(R.string.restart_named));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case Config.INTERACT_EVENT_WHAT_ANSWER_NAMED:
                Tools.log(TAG,"收到。。INTERACT_EVENT_WHAT_ANSWER_NAMED");
                updateDisplay();
                break;
        }
    }

    private void updateDisplay() {
        mViewHolder.mNamedPercent.setProgress(mNamedCount);
        mNamedTip = NAMED_ANSWER_TIP + mNamedCount + "名学生";
        mViewHolder.mNamedUsers.setText(textDiscolor(mNamedTip, NAMED_ANSWER_TIP.length(), mNamedTip.length()));
    }

    final class NamedCountViewHolder extends TitleActivity.ViewHolder {

        private CirclePercentView mNamedPercent;
        private TextView mOnlineUsers;
        private TextView mNamedUsers;
        private Button mRestartNamed;

        NamedCountViewHolder(View view) {
            super(view);
            mNamedPercent = view.findViewById(R.id.id_named_count_percent);
            mOnlineUsers = view.findViewById(R.id.id_named_count_onlines);
            mNamedUsers = view.findViewById(R.id.id_named_count_done);
            mRestartNamed = view.findViewById(R.id.id_named_count_restart);
            mRestartNamed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    go(NamedActivity.class);
                }
            });
        }


    }

}
