package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * @author cc
 * @Description CCDocRelativeLayout
 */
public class CCDocRelativeLayout extends RelativeLayout {
    private boolean intercept;
    private boolean canDraw;

    public CCDocRelativeLayout(Context context) {
        super(context);
    }

    public CCDocRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CCDocRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float downX, downY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (canDraw) return false;
        if (ev.getPointerCount() > 1 || intercept) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onDownListener != null)
                    onDownListener.onDown(ev);
                clickTime = SystemClock.uptimeMillis();
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs((ev.getX() - downX)) > Math.abs(ev.getY() - downY)) {
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                // 是点击事件
                boolean dis = Math.abs(ev.getX() - downX) <= scaledTouchSlop && Math.abs(ev.getY() - downY) <= scaledTouchSlop;
                if (dis && SystemClock.uptimeMillis() - clickTime < 300) {
                    if (onClickListener != null)
                        onClickListener.onClick();
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    private long clickTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!intercept) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                clickTime = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                // 是点击事件
                boolean dis = Math.abs(event.getX() - downX) <= scaledTouchSlop && Math.abs(event.getY() - downY) <= scaledTouchSlop;
                if (dis && SystemClock.uptimeMillis() - clickTime < 300) {
                    if (onClickListener != null)
                        onClickListener.onClick();
                }
                break;

        }
        return true;
    }


    private OnDownListener onDownListener;

    public void setOnDownListener(OnDownListener onDownListener) {
        this.onDownListener = onDownListener;
    }

    public void setCanDraw(boolean b) {
        this.canDraw = b;
    }

    public interface OnDownListener {
        void onDown(MotionEvent motionEvent);
    }

    private OnClickListener onClickListener;

    public void setClickEventListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }
}