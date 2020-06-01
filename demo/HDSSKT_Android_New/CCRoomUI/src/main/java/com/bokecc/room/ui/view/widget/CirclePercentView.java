package com.bokecc.room.ui.view.widget;

/**
 * 作者 ${CC视频}.<br/>
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


import com.bokecc.ccdocview.DensityUtil;
import com.bokecc.room.ui.R;

import java.text.NumberFormat;

public class CirclePercentView extends View {
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;

    protected Paint textPaint;
    protected Paint innerBottomTextPaint;

    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();

    private boolean showText;
    private float textSize;
    private int textColor;
    private int innerBottomTextColor;
    private float progress = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private int startingDegree;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private int innerBackgroundColor;
    private String text = null;
    private float innerBottomTextSize;
    private String innerBottomText;
    private float innerBottomTextMarginTop;

    private final float default_stroke_width;
    private final int default_finished_color = Color.rgb(66, 145, 241);
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_inner_bottom_text_color = Color.rgb(66, 145, 241);
    private final int default_inner_background_color = Color.TRANSPARENT;
    private final int default_max = 100;
    private final int default_startingDegree = 0;
    private final float default_text_size;
    private final float default_inner_bottom_text_size;
    private final float default_inner_bottom_text_margin_top;
    private final int min_size;

    public CirclePercentView(Context context) {
        this(context, null);
    }

    public CirclePercentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = DensityUtil.sp2px(context, 18);
        min_size = DensityUtil.dp2px(context, 100);
        default_stroke_width = DensityUtil.dp2px(context, 10);
        default_inner_bottom_text_size = DensityUtil.sp2px(context, 18);
        default_inner_bottom_text_margin_top = DensityUtil.dp2px(context, 100);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CirclePercentView, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters() {
        if (showText) {
            textPaint = new TextPaint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);

            innerBottomTextPaint = new TextPaint();
            innerBottomTextPaint.setColor(innerBottomTextColor);
            innerBottomTextPaint.setTextSize(innerBottomTextSize);
            innerBottomTextPaint.setAntiAlias(true);
        }

        finishedPaint = new Paint();
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(innerBackgroundColor);
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.CirclePercentView_cp_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.CirclePercentView_cp_unfinished_color, default_unfinished_color);
        showText = attributes.getBoolean(R.styleable.CirclePercentView_cp_show_text, true);

        setMax(attributes.getInt(R.styleable.CirclePercentView_cp_max, default_max));
        setProgress(attributes.getFloat(R.styleable.CirclePercentView_cp_progress, 0));
        finishedStrokeWidth = attributes.getDimension(R.styleable.CirclePercentView_cp_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.CirclePercentView_cp_unfinished_stroke_width, default_stroke_width);

        if (showText) {
            if (attributes.getString(R.styleable.CirclePercentView_cp_text) != null) {
                text = attributes.getString(R.styleable.CirclePercentView_cp_text);
            }

            textColor = attributes.getColor(R.styleable.CirclePercentView_cp_text_color, default_text_color);
            textSize = attributes.getDimension(R.styleable.CirclePercentView_cp_text_size, default_text_size);
            innerBottomTextSize = attributes.getDimension(R.styleable.CirclePercentView_cp_inner_bottom_text_size, default_inner_bottom_text_size);
            innerBottomTextColor = attributes.getColor(R.styleable.CirclePercentView_cp_inner_bottom_text_color, default_inner_bottom_text_color);
            innerBottomText = attributes.getString(R.styleable.CirclePercentView_cp_inner_bottom_text);
            innerBottomTextMarginTop = attributes.getDimension(R.styleable.CirclePercentView_cp_inner_bottom_text_margin_top, default_inner_bottom_text_margin_top);
        }

        innerBottomTextSize = attributes.getDimension(R.styleable.CirclePercentView_cp_inner_bottom_text_size, default_inner_bottom_text_size);
        innerBottomTextColor = attributes.getColor(R.styleable.CirclePercentView_cp_inner_bottom_text_color, default_inner_bottom_text_color);
        innerBottomText = attributes.getString(R.styleable.CirclePercentView_cp_inner_bottom_text);

        startingDegree = attributes.getInt(R.styleable.CirclePercentView_cp_circle_starting_degree, default_startingDegree);
        innerBackgroundColor = attributes.getColor(R.styleable.CirclePercentView_cp_background_color, default_inner_background_color);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.finishedStrokeWidth = finishedStrokeWidth;
        this.invalidate();
    }

    public float getUnfinishedStrokeWidth() {
        return unfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
        this.invalidate();
    }

    private float getProgressAngle() {
        return getProgress() / (float) max * 360f;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            throw new IllegalArgumentException();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.invalidate();
    }

    public int getInnerBackgroundColor() {
        return innerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
        this.invalidate();
    }


    public String getInnerBottomText() {
        return innerBottomText;
    }

    public void setInnerBottomText(String innerBottomText) {
        this.innerBottomText = innerBottomText;
        this.invalidate();
    }


    public float getInnerBottomTextSize() {
        return innerBottomTextSize;
    }

    public void setInnerBottomTextSize(float innerBottomTextSize) {
        this.innerBottomTextSize = innerBottomTextSize;
        this.invalidate();
    }

    public int getInnerBottomTextColor() {
        return innerBottomTextColor;
    }

    public void setInnerBottomTextColor(int innerBottomTextColor) {
        this.innerBottomTextColor = innerBottomTextColor;
        this.invalidate();
    }

    public int getStartingDegree() {
        return startingDegree;
    }

    public void setStartingDegree(int startingDegree) {
        this.startingDegree = startingDegree;
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));

    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);
        finishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);
        unfinishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);

        float innerCircleRadius = (getWidth() - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f;
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, innerCirclePaint);
        canvas.drawArc(finishedOuterRect, getStartingDegree(), getProgressAngle(), false, finishedPaint);
        canvas.drawArc(unfinishedOuterRect, getStartingDegree() + getProgressAngle(), 360 - getProgressAngle(), false, unfinishedPaint);

        if (showText) {
            float textY;
            float textHeight = textPaint.descent() + textPaint.ascent();
            NumberFormat num = NumberFormat.getPercentInstance();
            num.setMinimumFractionDigits(0);
            num.setMaximumFractionDigits(2);
            String text = this.text != null ? this.text : num.format(progress * 1.0 / max);
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
            textY = (getWidth() - textHeight) / 2.0f;

            if (!TextUtils.isEmpty(getInnerBottomText())) {
                innerBottomTextPaint.setTextSize(innerBottomTextSize);
//                float bottomTextBaseline = getHeight() - innerBottomTextHeight - (textPaint.descent() + textPaint.ascent()) / 2;
                float bottomTextBaseline;
                if (textY == -1) {
                    textHeight = innerBottomTextPaint.descent() + innerBottomTextPaint.ascent();
                    bottomTextBaseline = (getWidth() - textHeight) / 2.0f;
                } else {
                    bottomTextBaseline = textY + innerBottomTextMarginTop;
                }
                canvas.drawText(getInnerBottomText(), (getWidth() - innerBottomTextPaint.measureText(getInnerBottomText())) / 2.0f, bottomTextBaseline, innerBottomTextPaint);
            }
        }

//        if (attributeResourceId != 0) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), attributeResourceId);
//            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2.0f, (getHeight() - bitmap.getHeight()) / 2.0f, null);
//        }
    }

}

