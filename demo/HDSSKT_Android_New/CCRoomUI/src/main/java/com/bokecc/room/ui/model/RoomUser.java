package com.bokecc.room.ui.model;


import com.bokecc.sskt.base.bean.CCUser;

/**
 * 作者 ${CC视频}.<br/>
 */

public class RoomUser {
    private CCUser mUser;
    private int mMaiIndex = -1; // 麦序

    public CCUser getUser() {
        return mUser;
    }

    public void setUser(CCUser user) {
        mUser = user;
    }

    public int getMaiIndex() {
        return mMaiIndex;
    }

    public void setMaiIndex(int maiIndex) {
        mMaiIndex = maiIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomUser roomUser = (RoomUser) o;

        return mUser != null ? mUser.equals(roomUser.mUser) : roomUser.mUser == null;

    }

    @Override
    public int hashCode() {
        return mUser != null ? mUser.hashCode() : 0;
    }
}
