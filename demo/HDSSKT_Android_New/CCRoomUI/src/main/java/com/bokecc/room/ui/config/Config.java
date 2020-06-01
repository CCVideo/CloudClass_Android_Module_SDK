package com.bokecc.room.ui.config;

import android.graphics.Bitmap;

import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.room.ui.model.RoomDes;

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

//    public static final String APP_VERSION = "1.0.0";
//
//    public static final String FORCE_KILL_ACTION = "force_kill_action";
//    public static final String FORCE_KILL_VALUE = "force_kill";

//    public static final String KEY_DOC_ID = "key_of_current_doc_id_";
//    public static final String KEY_DOC_POSITION = "key_of_current_doc_position";

    public static final long SPLASH_DELAY = 2 * 1000L;

    public static final int TEACHER_REQUEST_CODE = 100;
    public static final int LECTURE_REQUEST_CODE = 101;
    public static final int NAMED_REQUEST_CODE = 102;
    public static final int DOC_LIST_RESULT_CODE = 200;
    public static final int DOC_GRID_RESULT_CODE = 201;
    public static final int SELECT_RESULT_CODE = 202;
    public static final int LAYOUT_RESULT_CODE = 203;
    public static final int CITYSTATUS_RESULT_CODE = 204;
    public static final int CITYNAME = 205;
    public static final int DOC_BOARD_RESULT_CODE =206;
    public static final int REQUEST_SYSTEM_PICTURE_UPDATE = 207;

    public static final int INTERACT_EVENT_WHAT_ERROR = 0x0001;
    public static final int INTERACT_EVENT_WHAT_CHAT = 0x1000;
    public static final int INTERACT_EVENT_WHAT_USER_COUNT = 0x1001;
    public static final int INTERACT_EVENT_WHAT_USER_LIST = 0x1002;
    public static final int INTERACT_EVENT_WHAT_USER_GAG = 0x1003;
    public static final int INTERACT_EVENT_WHAT_ALL_GAG = 0x1004;
    public static final int INTERACT_EVENT_WHAT_QUEUE_MAI = 0x1005;
    public static final int INTERACT_EVENT_WHAT_KICK_OUT = 0x1006;
    public static final int INTERACT_EVENT_WHAT_UP_MAI = 0x1007;
    public static final int INTERACT_EVENT_WHAT_DOWN_MAI = 0x1008;
    public static final int INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE = 0x1009;
    public static final int INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE = 0x1010;
    public static final int INTERACT_EVENT_WHAT_STREAM_ADDED = 0x1012;
    public static final int INTERACT_EVENT_WHAT_STREAM_REMOVED = 0x1013;
    public static final int INTERACT_EVENT_WHAT_STREAM_ERROR = 0x1014;
    public static final int INTERACT_EVENT_WHAT_START_NAMED = 0x1015;
    public static final int INTERACT_EVENT_WHAT_ROLL_CALL_LIST = 0x1016;
    public static final int INTERACT_EVENT_WHAT_ANSWER_NAMED = 0x1017;
    public static final int INTERACT_EVENT_WHAT_SERVER_DISCONNECT = 0x1018;
    public static final int INTERACT_EVENT_WHAT_INVITE = 0x1019;
    public static final int INTERACT_EVENT_WHAT_INVITE_CANCEL = 0x1020;
    public static final int INTERACT_EVENT_WHAT_CLASS_STATUS_START = 0x1021;
    public static final int INTERACT_EVENT_WHAT_CLASS_STATUS_STOP = 0x1022;
    public static final int INTERACT_EVENT_WHAT_MAIN_VIDEO_FOLLOW = 0x1023;
    public static final int INTERACT_EVENT_WHAT_TEMPLATE = 0x1024;
    public static final int INTERACT_EVENT_WHAT_USER_AUDIO = 0x1025;
    public static final int INTERACT_EVENT_WHAT_USER_VIDEO = 0x1026;
    public static final int INTERACT_EVENT_WHAT_TEACHER_DOWN = 0x1027;
    public static final int INTERACT_EVENT_WHAT_ROOM_TIMER_START = 0x1028;
    public static final int INTERACT_EVENT_WHAT_ROOM_TIMER_STOP = 0x1029;
    public static final int INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_START = 0x1030;
    public static final int INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_STOP = 0x1031;
    public static final int INTERACT_EVENT_WHAT_ROOM_ROLL_CALL_RESULT = 0x1032;
    public static final int INTERACT_EVENT_WHAT_AUTH_DRAW = 0x1033;
    public static final int INTERACT_EVENT_WHAT_HANDUP = 0x1034;
    public static final int INTERACT_EVENT_WHAT_LOCK = 0x1035;
    public static final int INTERACT_EVENT_WHAT_SERVER_CONNECT = 0x1036;
    public static final int INTERACT_EVENT_WHAT_DEVICE_FAIL = 0x1037;
    public static final int INTERACT_EVENT_WHAT_INTERLUDE_MEDIA = 0x1038;
    public static final int INTERACT_EVENT_WHAT_SETUP_THEACHER = 0x1039;
    public static final int INTERACT_EVENT_WHAT_SETUP_THEACHER_PAGE = 0x1040;
    public static final int INTERACT_EVENT_WHAT_DOC_CHANGE = 0x1041;
    public static final int INTERACT_EVENT_WHAT_TEACHER_SETUPTHEACHER_FLAG = 0x1042;
    public static final int INTERACT_EVENT_WHAT_NOTIFY_PUBLISH = 0x1043;
    public static final int INTERACT_EVENT_WHAT_INTERRUPT_PUBLISH = 0x1044;
    public static final int INTERACT_EVENT_WHAT_VIDEO_CONTROL = 0x1045;
    public static final int INTERACT_EVENT_WHAT_WARM_VIDEO = 0x1046;
//    public static final int INTERACT_EVENT_WHAT_PAGECHANGE= 0x1047;
    public static final int INTERACT_EVENT_WHAT_BRAIN_STOM = 0x1048;
    public static final int INTERACT_EVENT_WHAT_BRAIN_STOM_STOP = 0x1049;
    public static final int INTERACT_EVENT_WHAT_BALLOT_START = 0x1050;
    public static final int INTERACT_EVENT_WHAT_BALLOT_RESULT = 0x1051;
    public static final int INTERACT_EVENT_WHAT_SEND_CUP = 0x1052;
    public static final int INTERACT_EVENT_WHAT_SEND_FLOWER = 0x1053;
    public static final int INTERACT_EVENT_WHAT_STREAM_START = 0x1054;
    public static final int INTERACT_EVENT_WHAT_ATLAS_SERVER_DISCONNECTED = 0x1055;
    public static final int INTERACT_EVENT_WHAT_PUBLISH_DISCONNECTED = 0x1056;
    public static final int INTERACT_EVENT_WHAT_FORCE_OUT = 0x1057;
    public static final int INTERACT_EVENT_WHAT_DOUBLE_TEACHER_FLAG = 0x1058;
    public static final int INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_AUTH = 0x1059;
    public static final int INTERACT_EVENT_WHAT_DOUBLE_TEACHER_IS_TEACHER = 0x1060;
    public static final int INTERACT_EVENT_WHAT_TALKER_AUDIO_STATUS = 0x1061;
    public static final int INTERACT_EVENT_WHAT_SWITCH_SPEAK_ON = 0x1062;
    public static final int INTERACT_EVENT_WHAT_SWITCH_SPEAK_OFF = 0x1063;
    public static final int INTERACT_EVENT_WHAT_MEDIA_SYNC = 0x1064;
    public static final int DOC_DEL = 0x2000;
    public static final int CHAT_IMG = 0x3000;

    public static final int INTERACT_EVENT_WHAT_STREAM_START_OPT = 0x1065;
    public static final int INTERACT_EVENT_WHAT_STREAM_STOP_OPT = 0x1066;
    public static final int INTERACT_EVENT_WHAT_STREAM_STOP_PREVIEW = 0x1067;
    public static final int INTERACT_EVENT_WHAT_STREAM_RELOAD_PREVIEW = 0x1068;
    public static final int INTERACT_EVENT_WHAT_STREAM_SWITCH_ERROR = 0x1069;
    public static final int INTERACT_EVENT_WHAT_STREAM_SWITCH_STUDENT_DOWNMAI = 0x1070;

    public static final int INTERACT_EVENT_WHAT_STREAM_INIT = 0x1071;

    public static final String MIRRORING_MODE = "mirror_mode";
    
    /**
     * 菜单默认显示时间
     */
    public static final long MENU_SHOW_TIME = 3000;

    public static String mRoomId, mUserId, mRole, ServerUrl;
    public static RoomDes mRoomDes;

    public static ArrayList<Integer> mResults = new ArrayList<>();
    public static boolean isCommit = false; // 是否提交
    public static ArrayList<Integer> mBallotResults  = new ArrayList<>();//投票结果
    public static String mBallotTitle;//投票标题
    public static int mBallotNum;//投票总人数
    public static ArrayList<String> mBallotContent = new ArrayList<>(); //投票的内容
    public static int mSeleteType;
    public static final Map<Integer, Bitmap> sDocBG = new HashMap<>();
    public static boolean isBallot = false;
    public static ArrayList<List<Ballot.Statisic>> mBallot = new ArrayList<>(); //投票的内容

}
