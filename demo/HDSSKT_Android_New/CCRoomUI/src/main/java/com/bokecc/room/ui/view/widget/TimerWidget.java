package com.bokecc.room.ui.view.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.common.utils.TimeUtil;

/**
 * 计时器组件
 * @author wy
 */
public class TimerWidget extends LinearLayout {

    private TextView mTimerValue;

    private boolean isStartTimer = false;
    private long mRoomTime;

    private Handler mRoomTimerHandler = new Handler();
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    public TimerWidget(Context context) {
        super(context);
        initView(context);
    }

    public TimerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TimerWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化控件
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_timer_widget_layout,this,true);
        mTimerValue = findViewById(R.id.id_student_timer_value);
    }

    /**
     * 开始计时
     * @param startTime
     * @param lastTime
     */
    public void showTimer(long startTime, long lastTime) {
        if (getVisibility() == View.VISIBLE) {
            stopCountDown();
        }
        long endTime = startTime + lastTime;
        mRoomTime = (endTime - System.currentTimeMillis()) / 1000;
        if (mRoomTime <= 0) {
            mRoomTime = 0;
            updateTimeTip();
            startAnimTip();
            return;
        }
        setVisibility(View.VISIBLE);
        mTimerValue.setTextColor(Color.parseColor("#FFFFFF"));
        updateTimeTip();
        startCountDown();
    }

    /**
     * 去掉计时器
     */
    public void dismissTimer() {
        if (getVisibility() == View.GONE) {
            return;
        }
        mAnimatorSet.cancel();
        stopCountDown();
        setVisibility(View.GONE);
    }

    /**
     * 更新倒计时
     */
    private void updateTimeTip() {
        if (mTimerValue != null) {
            mTimerValue.setText(TimeUtil.formatNamed99(mRoomTime));
        }
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        if (!isStartTimer) {
            isStartTimer = true;
            mRoomTimerHandler.postDelayed(mRoomTimerTask, 1000);
        }
    }

    /**
     * 结束倒计时
     */
    private void stopCountDown() {
        if (isStartTimer) {
            isStartTimer = false;
            mRoomTimerHandler.removeCallbacks(mRoomTimerTask);
        }
    }

    /**
     * 计时器线程
     */
    private final Runnable mRoomTimerTask = new Runnable() {
        @Override
        public void run() {
            if (!isStartTimer) {
                return;
            }
            mRoomTime -= 1;
            if (mRoomTime <= 0) {
                updateTimeTip();
                stopCountDown();
                startAnimTip();
                return;
            }
            updateTimeTip();
            mRoomTimerHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 开始动画提示
     */
    private void startAnimTip() {
        mTimerValue.setTextColor(Color.parseColor("#ffd72113"));
        ValueAnimator alphaAnimator = ObjectAnimator.ofInt(1, 100).
                setDuration(1000);
        alphaAnimator.setRepeatCount(-1);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int diff = 255 - (int) (255 * fraction);
                String alpha;
                if (diff < 10) {
                    alpha = "0" + diff;
                } else {
                    alpha = Integer.toHexString(diff);
                    if (alpha.length() == 1) {
                        alpha = "0" + alpha;
                    }
                }
                String color = "#" + alpha + "f93930";
                if (mTimerValue != null) {
                    mTimerValue.setTextColor(Color.parseColor(color));
                }
            }
        });
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimatorSet.playTogether(alphaAnimator);
        mAnimatorSet.start();
    }

}
