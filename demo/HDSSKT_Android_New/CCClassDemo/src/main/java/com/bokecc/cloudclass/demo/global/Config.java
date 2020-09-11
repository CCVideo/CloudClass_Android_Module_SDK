package com.bokecc.cloudclass.demo.global;

import com.bokecc.cloudclass.demo.entity.RoomDes;

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

    public static final int INTERACT_EVENT_WHAT_STREAM_START_OPT = 0x1065;
    public static final int INTERACT_EVENT_WHAT_STREAM_STOP_OPT = 0x1066;
    public static final int INTERACT_EVENT_WHAT_STREAM_STOP_PREVIEW = 0x1067;
    public static final int INTERACT_EVENT_WHAT_STREAM_RELOAD_PREVIEW = 0x1068;
    public static final int INTERACT_EVENT_WHAT_STREAM_SWITCH_ERROR = 0x1069;
    public static final int INTERACT_EVENT_WHAT_STREAM_SWITCH_STUDENT_DOWNMAI = 0x1070;

    public static String mRoomId, mUserId, mRole, ServerUrl;
    public static RoomDes mRoomDes;



}
