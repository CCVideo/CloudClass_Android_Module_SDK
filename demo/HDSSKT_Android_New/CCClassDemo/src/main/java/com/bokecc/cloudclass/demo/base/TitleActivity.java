package com.bokecc.cloudclass.demo.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.cloudclass.demo.R;

/**
 * 带标题的类
 * @param <V>
 */
public abstract class TitleActivity<V extends TitleActivity.ViewHolder> extends BaseActivity {

    private View mContentView;
    private TitleOptions.OnTitleClickListener mOnTitleClickListener;
    protected V mViewHolder;

    protected Toolbar mTitleBar;
    protected ImageView mLeft;
    protected TextView mTitle;
    protected TextView mRight;
    protected TextView mCityItemName;
    protected FrameLayout mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewHolder != null) {
            mViewHolder = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_title;
    }

    @Override
    protected void onViewCreated() {
        initView();

        mContent.removeAllViews();
        mContentView = LayoutInflater.from(this).inflate(getContentLayoutId(), null);
        mContent.addView(mContentView);
        mViewHolder = getViewHolder(mContentView);
        onBindViewHolder(mViewHolder);
    }

    private void initView(){
        mTitleBar = findViewById(R.id.id_title_tool_bar);
        mLeft = findViewById(R.id.id_list_back);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        });
        mTitle = findViewById(R.id.id_list_title);
        mRight = findViewById(R.id.id_list_right);
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClick();
            }
        });
        mCityItemName = findViewById(R.id.id_city_item_name);
        mCityItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightNameClick();
            }
        });
        mContent = findViewById(R.id.id_title_content_layout);
    }

    /**
     * 获取布局内容
     */
    protected abstract int getContentLayoutId();

    protected abstract V getViewHolder(View contentView);

    protected abstract void onBindViewHolder(V holder);

    void onLeftClick() {
        if (mOnTitleClickListener != null) {
            mOnTitleClickListener.onLeft();
        }
    }

    void onRightNameClick() {
        if (mOnTitleClickListener != null) {
            mOnTitleClickListener.onRight();
        }
    }

    void onRightClick() {
        if (mOnTitleClickListener != null) {
            mOnTitleClickListener.onRight();
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public void setTitleOptions(TitleOptions options) {
        mTitleBar.setTitle(""); // 屏蔽原始的标题
        setSupportActionBar(mTitleBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0f);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTitleBar.setElevation(0f);
        }

        if (options.leftResId != 0) {
            mLeft.setImageResource(options.leftResId);
        }
        if (options.rightResId != 0) {
            mRight.setBackgroundResource(options.rightResId);
        }
        if (!TextUtils.isEmpty(options.rightValue)) {
            mRight.setText(options.rightValue);
        }
        if (!TextUtils.isEmpty(options.title)) {
            mTitle.setText(options.title);
        }
        if (!TextUtils.isEmpty(options.cityName)) {
            mCityItemName.setText(options.cityName);
        }
        setLeftStatus(options.leftStatus);
        setTitleStatus(options.titleStatus);
        setRightStatus(options.rightStatus);
        setCityStatus(options.cityNameStatus);

        mOnTitleClickListener = options.onTitleClickListener;

    }

    protected boolean isRightEnable() {
        return mRight.isEnabled();
    }

    protected boolean isLeftEnable() {
        return mLeft.isEnabled();
    }

    protected void setLeftEnabled(boolean enabled) {
        mLeft.setEnabled(enabled);
    }

    @SuppressWarnings("ResourceType")
    protected void setRightEnabled(boolean enabled) {
        if (enabled) {
            mRight.setTextColor(getResources().getColorStateList(R.drawable.title_right_selector));
        } else {
            mRight.setTextColor(getResources().getColor(R.color.colorTitleRightPressed));
        }
        mRight.setEnabled(enabled);
    }

    protected void setTitle(String value) {
        if (TextUtils.isEmpty(value) || mTitle.getVisibility() == View.GONE
                || mTitle.getVisibility() == View.INVISIBLE) {
            return;
        }
        mTitle.setText(value);
    }

    /**
     * 设置左边状态
     */
    protected void setLeftStatus(int status) {
        if (status == TitleOptions.VISIBLE) {
            mLeft.setVisibility(View.VISIBLE);
        } else if (status == TitleOptions.INVISIBLE){
            mLeft.setVisibility(View.INVISIBLE);
        } else {
            mLeft.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右边状态
     */
    protected void setRightStatus(int status) {
        if (status == TitleOptions.VISIBLE) {
            mRight.setVisibility(View.VISIBLE);
        } else if (status == TitleOptions.INVISIBLE){
            mRight.setVisibility(View.INVISIBLE);
        } else {
            mRight.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题状态
     */
    protected void setTitleStatus(int status) {
        if (status == TitleOptions.VISIBLE) {
            mTitle.setVisibility(View.VISIBLE);
        } else if (status == TitleOptions.INVISIBLE){
            mTitle.setVisibility(View.INVISIBLE);
        } else {
            mTitle.setVisibility(View.GONE);
        }
    }
    /**
     * 设置标题状态
     */
    protected void setCityStatus(int status) {
        if (status == TitleOptions.VISIBLE) {
            mCityItemName.setVisibility(View.VISIBLE);
        } else if (status == TitleOptions.INVISIBLE){
            mCityItemName.setVisibility(View.INVISIBLE);
        } else {
            mCityItemName.setVisibility(View.GONE);
        }
    }

    public class ViewHolder {

        public ViewHolder(View view) {

        }
    }


}
