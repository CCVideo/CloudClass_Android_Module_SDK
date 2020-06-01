package com.bokecc.room.ui.view.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;

import com.bokecc.ccdocview.DensityUtil;
import com.bokecc.room.ui.R;


/**
 * 作者 ${CC视频}.<br/>
 * 开关按钮可以点击/滑动切换状态
 */

public class ToggleButton extends CompoundButton {

    private static final int DEFAULT_MARGIN = 2;
    private static final long DEFAULT_ANIMATION_DURATION = 300L;

    private static final double DEFAULT_RATIO = 2.0;

    // 动画
    private ObjectAnimator mProcessAnimator;
    // 动画进度
    private float mProcess;
    // 开关矩形 背景矩形 开关移动的矩形范围
    private RectF mThumbRectF, mBackRectF, mSafeRectF;
    // 画笔
    private Paint mPaint;
    // 开关移动
    private RectF mPresentThumbRectF;
    // 手指按压
    private float mStartX, mStartY, mLastX;
    // 边距
    private int mMargin;
    private boolean isMargin = false; // 是否设置margin margin优先级高于其他
    private int mMarginStart;
    private int mMarginEnd;
    private int mMarginTop;
    private int mMarginBottom;

    // 滑块的半径
    private int mThumbRadius;

    // 颜色
    private int mThumbColor;
    private int mThumbColorHov;
    private int mBackColor;
    private int mBackColorHov;

    private int mCurrBackColor, mNextBackColor;


    // 背景圆角
    private int mBackRadius;

    // 最小滑动距离
    private int mTouchSlop;
    // 点击超时
    private int mClickTimeout;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();

        // 初始化动画
        mProcessAnimator = ObjectAnimator.ofFloat(this, "process", 0, 0).setDuration(DEFAULT_ANIMATION_DURATION);
        mProcessAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
        // 初始化矩形
        mThumbRectF = new RectF();
        mBackRectF = new RectF();
        mSafeRectF = new RectF();
        mPresentThumbRectF = new RectF();

        mMargin = DensityUtil.dp2px(context, DEFAULT_MARGIN);
        mMarginStart = mMargin;
        mMarginTop = mMargin;
        mMarginEnd = mMargin;
        mMarginBottom = mMargin;

        mThumbColor = context.getResources().getColor(R.color.colorThumb);
        mThumbColorHov = context.getResources().getColor(R.color.colorThumbHov);
        mBackColor = context.getResources().getColor(R.color.colorBack);
        mBackColorHov = context.getResources().getColor(R.color.colorBackHov);

        TypedArray array = attrs == null ? null :
                context.obtainStyledAttributes(attrs, R.styleable.ToggleButton, defStyleAttr, 0);
        if (array != null) {
            int count = array.getIndexCount();
            for (int i = 0; i < count; i++) {
                int index = array.getIndex(i);
                if (index == R.styleable.ToggleButton_thumbMargin) {
                    isMargin = true;
                    mMargin = array.getDimensionPixelSize(index, DEFAULT_MARGIN);
                } else if (index == R.styleable.ToggleButton_thumbMarginTop) {
                    mMarginTop = array.getDimensionPixelSize(index, DEFAULT_MARGIN);
                } else if (index == R.styleable.ToggleButton_thumbMarginBottom) {
                    mMarginBottom = array.getDimensionPixelSize(index, DEFAULT_MARGIN);
                } else if (index == R.styleable.ToggleButton_thumbMarginStart) {
                    mMarginStart = array.getDimensionPixelSize(index, DEFAULT_MARGIN);
                } else if (index == R.styleable.ToggleButton_thumbMarginEnd) {
                    mMarginEnd = array.getDimensionPixelSize(index, DEFAULT_MARGIN);
                } else if (index == R.styleable.ToggleButton_thumbColor) {
                    mThumbColor = array.getColor(index, context.getResources().
                            getColor(R.color.colorThumb));
                } else if (index == R.styleable.ToggleButton_thumbColorHov) {
                    mThumbColorHov = array.getColor(index, context.getResources().
                            getColor(R.color.colorThumbHov));
                } else if (index == R.styleable.ToggleButton_backColor) {
                    mBackColor = array.getColor(index, context.getResources().
                            getColor(R.color.colorBack));
                } else if (index == R.styleable.ToggleButton_backColorHov) {
                    mBackColorHov = array.getColor(index, context.getResources().
                            getColor(R.color.colorBackHov));
                }
            }
            array.recycle();
        }

        // click
        array = attrs == null ? null : getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.focusable, android.R.attr.clickable});
        if (array != null) {
            boolean focusable = array.getBoolean(0, true);
            //noinspection ResourceType
            boolean clickable = array.getBoolean(1, focusable);
            setFocusable(focusable);
            setClickable(clickable);
            array.recycle();
        }

        mCurrBackColor = mBackColor;
        mNextBackColor = mBackColorHov;

        if (isChecked()) {
            setProcess(1);
            mCurrBackColor = mBackColorHov;
            mNextBackColor = mBackColor;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int WidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        double ratio = WidthSize * 1.0 / heightSize;
        ratio = ratio < DEFAULT_RATIO ? DEFAULT_RATIO : ratio;

        heightSize = (int) (WidthSize / ratio);
        mBackRadius = heightSize / 2;
        if (isMargin) {
            mThumbRadius = (heightSize - 2 * mMargin) / 2;
        } else {
            mThumbRadius = (heightSize - mMarginTop - mMarginBottom) / 2;
        }

        setMeasuredDimension(WidthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            setup();
        }
    }

    /**
     * 进行背景和滑块的区域设置
     */
    private void setup() {
        float thumbTop, thumbStart;
        if (isMargin) {
            thumbTop = getPaddingTop() + mMargin;
            thumbStart = getPaddingStart() + mMargin;
        } else {
            thumbTop = getPaddingTop() + mMarginTop;
            thumbStart = getPaddingStart() + mMarginStart;
        }

        mThumbRectF.set(thumbStart, thumbTop,
                thumbStart + mThumbRadius * 2, thumbTop + mThumbRadius * 2);

        mBackRectF.set(getPaddingStart(),
                getPaddingTop(),
                getPaddingStart() + getMeasuredWidth(),
                getPaddingEnd() + getMeasuredHeight());

        mSafeRectF.set(mThumbRectF.left, 0, mBackRectF.right - mMarginEnd - mThumbRectF.width(), 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int alpha;
        int colorAlpha;
        // 做一个颜色的交替
        alpha = (int) (255 * (isChecked() ? getProcess() : (1 - getProcess())));
        colorAlpha = Color.alpha(mCurrBackColor);
        colorAlpha = colorAlpha * alpha / 255;
        mPaint.setARGB(colorAlpha, Color.red(mCurrBackColor), Color.green(mCurrBackColor), Color.blue(mCurrBackColor));
        // 绘制圆角矩形
        canvas.drawRoundRect(mBackRectF, mBackRadius, mBackRadius, mPaint);

        alpha = 255 - alpha;
        colorAlpha = Color.alpha(mNextBackColor);
        colorAlpha = colorAlpha * alpha / 255;
        mPaint.setARGB(colorAlpha, Color.red(mNextBackColor), Color.green(mNextBackColor), Color.blue(mNextBackColor));
        canvas.drawRoundRect(mBackRectF, mBackRadius, mBackRadius, mPaint);

        mPaint.setAlpha(255);

        // 滑动的时候
        mPresentThumbRectF.set(mThumbRectF);
        // 进行矩形偏移
        mPresentThumbRectF.offset(mProcess * mSafeRectF.width(), 0);
        mPaint.setColor(mThumbColor);
        canvas.drawRoundRect(mPresentThumbRectF, mThumbRadius, mThumbRadius, mPaint);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (isChecked()) {
            mCurrBackColor = mBackColorHov;
            mNextBackColor = mBackColor;
        } else {
            mCurrBackColor = mBackColor;
            mNextBackColor = mBackColorHov;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled() || !isClickable() || !isFocusable()) {
            return false;
        }

        int action = event.getAction(); // 过去当前的手势

        float deltaX = event.getX() - mStartX;  // X轴移动的位移
        float deltaY = event.getY() - mStartY;  // Y轴移动的位移

        // 判断是否可以移动到下一个状态
        boolean nextStatus;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                catchView();
                mStartX = event.getX();
                mStartY = event.getY();
                mLastX = mStartX;
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                setProcess(getProcess() + (x - mLastX) / mSafeRectF.width());
                mLastX = x;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setPressed(false);
                nextStatus = getStatusBasedOnPos();
                float time = event.getEventTime() - event.getDownTime();
                if (deltaX < mTouchSlop && deltaY < mTouchSlop && time < mClickTimeout) {
                    performClick();
                } else {
                    if (nextStatus != isChecked()) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        setChecked(nextStatus);
                    } else {
                        animateToState(nextStatus);
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void setChecked(final boolean checked) {
        if (isChecked() != checked) {
            animateToState(checked);
        }
        super.setChecked(checked);
    }

    /**
     * 没有动画
     */
    public void setCheckedImmediately(boolean checked) {
        super.setChecked(checked);
        if (mProcessAnimator != null && mProcessAnimator.isRunning()) {
            mProcessAnimator.cancel();
        }
        setProcess(checked ? 1 : 0);
        invalidate();
    }

    private void catchView() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * 判断动画执行的状态
     */
    private boolean getStatusBasedOnPos() {
        return getProcess() > 0.5f;
    }

    /**
     * 进行动画状态切换
     */
    protected void animateToState(boolean checked) {
        if (mProcessAnimator == null) {
            return;
        }
        if (mProcessAnimator.isRunning()) {
            mProcessAnimator.cancel();
        }
        mProcessAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        if (checked) {
            mProcessAnimator.setFloatValues(mProcess, 1f);
        } else {
            mProcessAnimator.setFloatValues(mProcess, 0);
        }
        mProcessAnimator.start();
    }

    public final float getProcess() {
        return mProcess;
    }

    public final void setProcess(final float process) {
        float ap = process;
        if (ap > 1) {
            ap = 1;
        } else if (ap < 0) {
            ap = 0;
        }
        this.mProcess = ap;
        invalidate();
    }

}
