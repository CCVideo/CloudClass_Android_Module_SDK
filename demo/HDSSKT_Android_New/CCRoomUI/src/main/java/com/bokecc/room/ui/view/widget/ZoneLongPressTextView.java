package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 作者 ${CC视频}.<br/>
 */

public class ZoneLongPressTextView extends TextView {

    private static final String TAG = ZoneLongPressTextView.class.getSimpleName();

    private float mOffset;
    private float mWidth;
    private boolean isLongPress;
    private OnZoneLongPressedListener mOnZoneLongPressedListener;

    public ZoneLongPressTextView(Context context) {
        this(context, null);
    }

    public ZoneLongPressTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoneLongPressTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isLongPress) {
                    if (mOnZoneLongPressedListener != null) {
                        mOnZoneLongPressedListener.onLongClick(v);
                    }
                }
                return false;
            }
        });
    }

    public void setDefauleOffset(int offset) { // 配置 drawable 使用
        mOffset = offset;
    }

    public void setLongPressZone(int start, int end) {
        if (start < 0 || end > getText().length() || (start >= end)) {
            throw new IllegalArgumentException();
        }
        String offsetText = getText().subSequence(0, start).toString();
        String measureText = getText().subSequence(start, end).toString();
        mOffset += getPaint().measureText(offsetText);
        mWidth = getPaint().measureText(measureText);
    }

    public void setOnZoneLongPressedListener(OnZoneLongPressedListener onZoneLongPressedListener) {
        mOnZoneLongPressedListener = onZoneLongPressedListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        isLongPress = event.getX() > mOffset && event.getX() < (mOffset + mWidth);
        // TODO: 2017/7/18 进行区域判断
        isLongPress = !(event.getY() > 0 && event.getY() < (getLineHeight() + getLineSpacingExtra())) || event.getX() > mOffset;
        return super.dispatchTouchEvent(event);
    }

    public interface OnZoneLongPressedListener {
        void onLongClick(View v);
    }

}
