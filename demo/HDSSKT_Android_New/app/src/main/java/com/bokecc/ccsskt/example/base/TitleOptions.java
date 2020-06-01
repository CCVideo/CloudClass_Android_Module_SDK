package com.bokecc.ccsskt.example.base;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者 ${CC视频}.<br/>
 */

public class TitleOptions {

    public int leftResId, rightResId;
    public int leftStatus, rightStatus, titleStatus, cityNameStatus;
    public String title, rightValue, cityName;
    public OnTitleClickListener onTitleClickListener;

    private TitleOptions() {}

    public static final class Builder {

        private int leftResId = 0, rightResId = 0;
        private int leftStatus = GONE, rightStatus = GONE, titleStatus = GONE, cityNameStatus =
                GONE;
        private String title = "", rightValue = "",cityName = "";
        OnTitleClickListener onTitleClickListener = null;

        public Builder leftStatus(@TitleComponentStatus int status) {
            this.leftStatus = status;
            return this;
        }

        public Builder rightStatus(@TitleComponentStatus int status) {
            this.rightStatus = status;
            return this;
        }

        public Builder leftResId(int resId) {
            this.leftResId = resId;
            return this;
        }

        public Builder rightResId(int resId) {
            this.rightResId = resId;
            return this;
        }

        public Builder titleStatus(@TitleComponentStatus int status) {
            this.titleStatus = status;
            return this;
        }
        public Builder cityStatus(@TitleComponentStatus int status) {
            this.cityNameStatus = status;
            return this;
        }
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder city(String city) {
            this.cityName = city;
            return this;
        }
        public Builder rightValue(String value) {
            this.rightValue = value;
            return this;
        }

        public Builder onTitleClickListener(OnTitleClickListener onTitleClickListener) {
            this.onTitleClickListener = onTitleClickListener;
            return this;
        }

        public TitleOptions build() {
            TitleOptions options = new TitleOptions();
            options.leftResId = this.leftResId;
            options.rightResId = this.rightResId;
            options.leftStatus = this.leftStatus;
            options.rightStatus = this.rightStatus;
            options.titleStatus = this.titleStatus;
            options.cityNameStatus = this.cityNameStatus;
            options.title = this.title;
            options.cityName = this.cityName;
            options.rightValue = this.rightValue;
            options.onTitleClickListener = this.onTitleClickListener;
            return options;
        }

    }

    public static final int INVISIBLE = 1;
    public static final int VISIBLE = 0;
    public static final int GONE = 2;

    @IntDef({INVISIBLE, VISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE) //注解保留范围为源代码
    public @interface TitleComponentStatus {
    }

    public interface OnTitleClickListener {
        void onLeft();
        void onRight();
    }

    public static abstract class OnLeftClickListener implements OnTitleClickListener {
        @Override
        public void onRight() {
            // Ignore
        }
        @Override
        public void onLeft() {
            // Ignore
        }
    }

    public static abstract class OnRightClickListener implements OnTitleClickListener {
        @Override
        public void onLeft() {
            // Ignore
        }
    }

}
