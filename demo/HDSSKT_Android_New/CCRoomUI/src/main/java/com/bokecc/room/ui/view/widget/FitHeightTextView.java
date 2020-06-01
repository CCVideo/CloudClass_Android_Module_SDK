package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * textview 自动换行
 * Created by wdh on 2018/6/17.
 */

public class FitHeightTextView extends android.support.v7.widget.AppCompatTextView {
    private StaticLayout myStaticLayout;
    private TextPaint tp;

    public FitHeightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView();
    }

    private void initView() {
        tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp.setTextSize(getTextSize());
        tp.setColor(getCurrentTextColor());
        myStaticLayout = new StaticLayout(getText(), tp, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        myStaticLayout.draw(canvas);
    }

}
