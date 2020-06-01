package com.bokecc.room.ui.model;

/**
 * 作者 ${CC视频}.<br/>
 */

public class NamedTime {
    private int mSeconds;
    private boolean isSelected;

    public NamedTime(int seconds, boolean isSelected) {
        mSeconds = seconds;
        this.isSelected = isSelected;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
