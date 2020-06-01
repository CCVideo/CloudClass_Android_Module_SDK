package com.bokecc.room.ui.model;

/**
 * 作者 ${CC视频}.<br/>
 */

public class IconEntity {
    private int resId;
    private String resDes;

    public IconEntity() {
    }

    public IconEntity(int resId, String resDes) {
        this.resId = resId;
        this.resDes = resDes;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResDes() {
        return resDes;
    }

    public void setResDes(String resDes) {
        this.resDes = resDes;
    }

}
