package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bokecc.room.ui.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 作者 ${CC视频}.<br/>
 */
public class ItemLayout extends FrameLayout {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int BOTH = 2;
    public static final int NONE = 3;

    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    public static final int GONE = 2;

    private Context mContext;

    private View mTopLineView;
    private View mBottomLineView;
    private TextView mTipView;
    private TextView mValueView;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;

    private View mItemView;

    @IntDef({TOP, BOTTOM, BOTH, NONE})
    @Retention(RetentionPolicy.SOURCE) //注解保留范围为源代码
    public @interface LineMode {
    }

    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE) //注解保留范围为源代码
    public @interface Visibility {
    }

    // 左右icon的显示属性
    private int mLeftVisibility = GONE, mRightVisibility = GONE;
    // 左右icon资源
    private int mLeftResId = -1, mRightResId = -1;
    // 左右icon的左右边距
    private int mLeftMarginStart = 0, mLeftMarginEnd = 0,
            mRightMarginStart = 0, mRightMarginEnd = 0;
    // 提示 和 值 的显示属性
    private int mTipVisibility = VISIBLE, mValueVisibility = VISIBLE;
    // 提示 值 分割线的颜色
    private int mTipColor = Color.BLACK, mValueColor = Color.BLACK,
            mTopLineColor = Color.BLACK, mBottomLineColor = Color.BLACK;
    // 提示 和 值 的文字尺寸
    private int mTipSize = 12, mValueSize = 12;
    // 提示左边距 值右边距
    private int mTipMarginStart = 0, mValueMarginEnd = 0;
    // 提示 和 值 的默认显示数据
    private String mTip = "", mValue = "";
    // 分割线的显示属性
    private int mLineMode = BOTH;
    // 分割线的左右边距
    private int mTopLineMarginStart = 0, mTopLineMarginEnd = 0,
            mBottomLineMarginStart = 0, mBottomLineMarginEnd = 0;

    public ItemLayout(Context context) {
        this(context, null);
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray array = attrs == null ? null :
                context.obtainStyledAttributes(attrs, R.styleable.ItemLayout, defStyleAttr, 0);
        if (array != null) {
            int count = array.getIndexCount();
            for (int i = 0; i < count; i++) {
                int index = array.getIndex(i);
                if (index == R.styleable.ItemLayout_leftSrc) {
                    mLeftResId = array.getResourceId(index, -1);
                } else if (index == R.styleable.ItemLayout_leftMarginStart) {
                    mLeftMarginStart = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_leftMarginEnd) {
                    mLeftMarginEnd = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_leftVisbility) {
                    mLeftVisibility = array.getInt(index, GONE);
                } else if (index == R.styleable.ItemLayout_rightSrc) {
                    mRightResId = array.getResourceId(index, -1);
                } else if (index == R.styleable.ItemLayout_rightMarginStart) {
                    mRightMarginStart = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_rightMarginEnd) {
                    mRightMarginEnd = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_rightVisbility) {
                    mRightVisibility = array.getInt(index, GONE);
                } else if (index == R.styleable.ItemLayout_tipTxt) {
                    mTip = array.getString(index);
                } else if (index == R.styleable.ItemLayout_tipSize) {
                    mTipSize = array.getDimensionPixelSize(index,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f,
                                    context.getResources().getDisplayMetrics()));
                } else if (index == R.styleable.ItemLayout_tipColor) {
                    mTipColor = array.getColor(index, Color.BLACK);
                } else if (index == R.styleable.ItemLayout_tipMarginStart) {
                    mTipMarginStart = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_tipVisbility) {
                    mTipVisibility = array.getInt(index, GONE);
                } else if (index == R.styleable.ItemLayout_valueTxt) {
                    mValue = array.getString(index);
                } else if (index == R.styleable.ItemLayout_valueSize) {
                    mValueSize = array.getDimensionPixelSize(index,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f,
                                    context.getResources().getDisplayMetrics()));
                } else if (index == R.styleable.ItemLayout_valueColor) {
                    mValueColor = array.getColor(index, Color.BLACK);
                } else if (index == R.styleable.ItemLayout_valueMarginEnd) {
                    mValueMarginEnd = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_valueVisbility) {
                    mValueVisibility = array.getInt(index, GONE);
                } else if (index == R.styleable.ItemLayout_lineMode) {
                    mLineMode = array.getInt(index, BOTH);
                } else if (index == R.styleable.ItemLayout_bottomLineColor) {
                    mBottomLineColor = array.getColor(index, Color.BLACK);
                } else if (index == R.styleable.ItemLayout_bottomLineMarginEnd) {
                    mBottomLineMarginEnd = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_bottomLineMarginStart) {
                    mBottomLineMarginStart = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_topLineColor) {
                    mTopLineColor = array.getColor(index, Color.BLACK);
                } else if (index == R.styleable.ItemLayout_topLineMarginEnd) {
                    mTopLineMarginEnd = array.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.ItemLayout_topLineMarginStart) {
                    mTopLineMarginStart = array.getDimensionPixelSize(index, 0);
                }
            }
        }

        initItemMainLayout();
    }

    /**
     * 初始化主要布局
     */
    private void initItemMainLayout() {
        mItemView = LayoutInflater.from(mContext).
                inflate(R.layout.item_layout, null);
        addView(mItemView);
        initViews();
        // aa. 值设置
        setLeftImageResource(mLeftResId);
        setTipTxtSize(mTipSize);
        setTipTxtColor(mTipColor);
        setTip(mTip);
        setValueTxtSize(mValueSize);
        setValueTxtColor(mValueColor);
        setValue(mValue);
        setRightImageResource(mRightResId);
        mTopLineView.setBackgroundColor(mTopLineColor);
        mBottomLineView.setBackgroundColor(mBottomLineColor);
        // 2. 显示设置
        setDivideLineMode(mLineMode); // 分割线
        setLeftVisibility(mLeftVisibility); // lefticon
        setRightVisibility(mRightVisibility); // righticon
        setTipVisibility(mTipVisibility); // tip
        setValueVisibility(mValueVisibility); // value
        // 3. 边距设置
        setTopLineMargin(mTopLineMarginStart, mTopLineMarginEnd);
        setBottomLineMargin(mBottomLineMarginStart, mBottomLineMarginEnd);
        setLeftMargin(mLeftMarginStart, mLeftMarginEnd);
        setTipMarginStart(mTipMarginStart);
        setValueMarginEnd(mValueMarginEnd);
        setRightMargin(mRightMarginStart, mRightMarginEnd);
        setClickable(true);
    }

    /**
     * 初始化子view
     */
    private void initViews() {
        mTopLineView = findById(R.id.id_item_top_line);
        mBottomLineView = findById(R.id.id_item_bottom_line);
        mTipView = (TextView) findById(R.id.id_item_tip);
        mValueView = (TextView) findById(R.id.id_item_value);
        mLeftIcon = (ImageView) findById(R.id.id_item_left);
        mRightIcon = (ImageView) findById(R.id.id_item_right);
    }

    /**
     * 通过id查找view
     *
     * @param id view id
     * @return {@link View}
     */
    private View findById(int id) {
        return mItemView.findViewById(id);
    }

    /**
     * 设置分割线显示的模式
     *
     * @param lineMode 分割线的模式 {@link ItemLayout} <ul><li>TOP</li><li>BOTTOM</li><li>MIEDIA_MODE_BOTH</li></ul>
     */
    public void setDivideLineMode(@LineMode int lineMode) {
        if (lineMode == TOP) {
            mTopLineView.setVisibility(View.VISIBLE);
            mBottomLineView.setVisibility(View.GONE);
        } else if (lineMode == BOTTOM) {
            mTopLineView.setVisibility(View.GONE);
            mBottomLineView.setVisibility(View.VISIBLE);
        } else if (lineMode == BOTH) {
            mTopLineView.setVisibility(View.VISIBLE);
            mBottomLineView.setVisibility(View.VISIBLE);
        } else if (lineMode == NONE) {
            mTopLineView.setVisibility(View.GONE);
            mBottomLineView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置左边的图片
     *
     * @param resId 图片id
     */
    public void setLeftImageResource(int resId) {
        if (resId > 0) {
            mLeftIcon.setImageResource(resId);
        }
    }

    /**
     * 设置lefticon显示状态
     *
     * @param leftVisibility 状态 {@link Visibility}
     */
    public void setLeftVisibility(@Visibility int leftVisibility) {
        if (leftVisibility == VISIBLE) {
            mLeftIcon.setVisibility(View.VISIBLE);
        } else if (leftVisibility == INVISIBLE) {
            mLeftIcon.setVisibility(View.INVISIBLE);
        } else if (leftVisibility == GONE) {
            mLeftIcon.setVisibility(View.GONE);
        } else {
            throw new IllegalArgumentException("setLeftVisibility");
        }
    }

    /**
     * 设置右边的图片
     *
     * @param resId 图片id
     */
    public void setRightImageResource(int resId) {
        if (resId > 0) {
            mRightIcon.setImageResource(resId);
        }
    }

    /**
     * 设置righticon显示状态
     *
     * @param rightVisibility 状态 {@link Visibility}
     */
    public void setRightVisibility(@Visibility int rightVisibility) {
        if (rightVisibility == VISIBLE) {
            mRightIcon.setVisibility(View.VISIBLE);
        } else if (rightVisibility == INVISIBLE) {
            mRightIcon.setVisibility(View.INVISIBLE);
        } else if (rightVisibility == GONE) {
            mRightIcon.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mValueView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            mValueView.setLayoutParams(params);
        } else {
            throw new IllegalArgumentException("setRightVisibility");
        }
    }

    /**
     * 设置上分割线的边距
     *
     * @param start 左边距 单位px
     * @param end   右边距
     */
    public void setTopLineMargin(int start, int end) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTopLineView.getLayoutParams();
        params.leftMargin = start;
        params.rightMargin = end;
        mTopLineView.setLayoutParams(params);
    }

    /**
     * 设置下分割线的边距
     *
     * @param start 左边距 单位px
     * @param end   右边距
     */
    public void setBottomLineMargin(int start, int end) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomLineView.getLayoutParams();
        params.leftMargin = start;
        params.rightMargin = end;
        mBottomLineView.setLayoutParams(params);
    }

    /**
     * 设置lefticon左右边距
     *
     * @param start 左边距 单位px
     * @param end   右边距
     */
    public void setLeftMargin(int start, int end) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLeftIcon.getLayoutParams();
        params.leftMargin = start;
        params.rightMargin = end;
        mLeftIcon.setLayoutParams(params);
    }

    /**
     * 设置righticon左右边距
     *
     * @param start 左边距 单位px
     * @param end   右边距
     */
    public void setRightMargin(int start, int end) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightIcon.getLayoutParams();
        params.leftMargin = start;
        params.rightMargin = end;
        mRightIcon.setLayoutParams(params);
    }

    /**
     * 设置tipview左边距
     *
     * @param start 边距单位px
     */
    public void setTipMarginStart(int start) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTipView.getLayoutParams();
        params.setMarginStart(start);
        mTipView.setLayoutParams(params);
    }

    /**
     * 设置valueview右边距
     *
     * @param end 边距单位px
     */
    public void setValueMarginEnd(int end) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mValueView.getLayoutParams();
        params.setMarginEnd(end);
        mValueView.setLayoutParams(params);
    }

    /**
     * 设置item提示
     *
     * @param value 提示
     */
    public void setTip(String value) {
        if (!TextUtils.isEmpty(value)) {
            mTipView.setText(value);
        }
    }

    /**
     * 设置tip显示状态
     *
     * @param tipVisibility 状态 {@link Visibility}
     */
    public void setTipVisibility(@Visibility int tipVisibility) {
        if (tipVisibility == VISIBLE) {
            mTipView.setVisibility(View.VISIBLE);
        } else if (tipVisibility == INVISIBLE) {
            mTipView.setVisibility(View.INVISIBLE);
        } else if (tipVisibility == GONE) {
            mTipView.setVisibility(View.GONE);
        } else {
            throw new IllegalArgumentException("setTipVisibility");
        }
    }

    /**
     * 设置item当前属性值
     *
     * @param value 值
     */
    public void setValue(String value) {
        if (value != null) {
            mValueView.setText(value);
        }
    }

    /**
     * 设置value显示状态
     *
     * @param valueVisibility 状态 {@link Visibility}
     */
    public void setValueVisibility(@Visibility int valueVisibility) {
        if (valueVisibility == VISIBLE) {
            mValueView.setVisibility(View.VISIBLE);
        } else if (valueVisibility == INVISIBLE) {
            mValueView.setVisibility(View.INVISIBLE);
        } else if (valueVisibility == GONE) {
            mValueView.setVisibility(View.GONE);
        } else {
            throw new IllegalArgumentException("setValueVisibility");
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param color 颜色
     */
    public void setTipTxtColor(int color) {
        mTipView.setTextColor(color);
    }

    /**
     * 设置值文字颜色
     *
     * @param color 颜色
     */
    public void setValueTxtColor(int color) {
        mValueView.setTextColor(color);
    }

    /**
     * 设置提示文字尺寸
     *
     * @param size 尺寸 单位px
     */
    public void setTipTxtSize(int size) {
        mTipView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    /**
     * 设置值文字尺寸
     *
     * @param size 尺寸 单位px
     */
    public void setValueTxtSize(int size) {
        mValueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

}
