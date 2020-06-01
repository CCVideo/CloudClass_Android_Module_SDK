package com.bokecc.room.ui.listener;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.bokecc.common.utils.DensityUtil;

/**
 * @author cc
 * @Description  拖拽 TouchListener
 */
public class DragTouchListener implements View.OnTouchListener{

    private Context mContext;
    private View targetView;
    private ViewGroup.MarginLayoutParams params;
    private DragOnclickListener mOnclickListener;

    public DragTouchListener(Context mContext, View view) {
        this.mContext = mContext;
        this.targetView = view;
        params = (ViewGroup.MarginLayoutParams) targetView.getLayoutParams();
    }

    private int lastX, lastY;
    private long clickTime;

    public void setOnClickListener(DragOnclickListener mOnclickListener){
        this.mOnclickListener = mOnclickListener;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下时
                lastX = x;
                lastY = y;
                clickTime = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_MOVE://手指移动时
                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;
                params.topMargin = params.topMargin + offY;
                params.leftMargin = params.leftMargin + offX;

//                params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                if (params.topMargin < 0) {
                    params.topMargin = 0;
                }
                if (params.leftMargin < 0) {
                    params.leftMargin = 0;
                }
                if (params.topMargin > (DensityUtil.getHeight(mContext) - targetView.getHeight())) {
                    params.topMargin = DensityUtil.getHeight(mContext) - targetView.getHeight();
                }
                if (params.leftMargin > (DensityUtil.getWidth(mContext) - targetView.getWidth())) {
                    params.leftMargin = DensityUtil.getWidth(mContext) - targetView.getWidth();
                }
                targetView.setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                int scaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
                // 是点击事件
                boolean dis = Math.abs(event.getX() - lastX) <= scaledTouchSlop && Math.abs(event.getY() - lastY) <= scaledTouchSlop;
                if (dis && SystemClock.uptimeMillis() - clickTime < 300) {
                    if(mOnclickListener!=null)
                        mOnclickListener.onClick();
                }
                break;

        }
        return true;
    }
    public interface DragOnclickListener{
        void onClick();
    }
}
