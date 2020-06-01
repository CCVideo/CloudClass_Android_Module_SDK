package com.bokecc.room.ui.view.userList;

import com.bokecc.room.ui.model.RoomUser;

import java.util.Comparator;

/**
 * 作者 ${CC视频}.<br/>
 */

public class RoomUserComparator implements Comparator<RoomUser> {

    @Override
    public int compare(RoomUser o1, RoomUser o2) {
        try {
            long curRequestTime = (long) Double.parseDouble(o1.getUser().getRequestTime());
            long compareRequestTime = (long) Double.parseDouble(o2.getUser().getRequestTime());
            return (int) (curRequestTime - compareRequestTime);
        } catch (Exception e) {
            return 1;
        }
    }

}
