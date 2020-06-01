package com.bokecc.room.ui.utils;

import com.bokecc.sskt.base.bean.CCUser;

import java.util.Comparator;

/**
 * 作者 ${CC视频}.<br/>
 */

public class UserComparator implements Comparator<CCUser> {

    @Override
    public int compare(CCUser o1, CCUser o2) {
        long curRequestTime = (long) Double.parseDouble(o1.getRequestTime());
        long compareRequestTime = (long) Double.parseDouble(o2.getRequestTime());
        return (int) (curRequestTime - compareRequestTime);
    }
}
