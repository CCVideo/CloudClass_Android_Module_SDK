package com.bokecc.ccsskt.example.global;

import android.graphics.Bitmap;

import com.bokecc.ccsskt.example.entity.RoomDes;
import com.bokecc.sskt.base.bean.Ballot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者 ${CC视频}.<br/>
 */

public class Config {


    private Config() {
        throw new UnsupportedOperationException();
    }

    public static final String SP_NAME = "com_bokecc_sskt_sp";

    public static final int CITYSTATUS_RESULT_CODE = 204;
    public static final int CITYNAME = 205;

    public static String mRoomId, mUserId, mRole, ServerUrl;
    public static RoomDes mRoomDes;



}
