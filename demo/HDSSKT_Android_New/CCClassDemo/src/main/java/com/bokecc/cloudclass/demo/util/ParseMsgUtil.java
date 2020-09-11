package com.bokecc.cloudclass.demo.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bokecc.cloudclass.demo.entity.RoomDes;
import com.bokecc.cloudclass.demo.global.Config;
import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCCityInteractBean;
import com.bokecc.sskt.base.bean.CCCityListSet;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.bokecc.cloudclass.demo.activity.DirectionActivity.startSelf;

/**
 * 作者 ${CC视频}.<br/>
 */

public class ParseMsgUtil {
    private ParseMsgUtil() {
    }

    public static void parseUrl(final Context context, String url, final ParseCallBack callBack) {
        try {
            if (callBack != null) {
                callBack.onStart();
            }
            if (TextUtils.isEmpty(url)) {
                throw new NullPointerException("课堂链接错误");
            }

            Map<String,String> map =Tools.getQueryParams(url);
            final String template = map.get("template");
            Config.mUserId = map.get("userid");
            Config.mRoomId = map.get("roomid");
            Config.mRole = map.get("role");
            if(TextUtils.isEmpty(Config.mRole)){
                try{
                    Log.i(TAG, url);
                    String arr[] = url.split("\\?|&");
                    String hosts[] = (arr[0].substring("https://".length(), arr[0].length())).split("/");
                    Config.ServerUrl = hosts[hosts.length - 3];
                    Config.mRole = hosts[hosts.length - 1];
                }catch (Exception e){
                    e.printStackTrace();
                    Tools.handleException(e);
                }

            }
            if(TextUtils.isEmpty(Config.mUserId)||TextUtils.isEmpty(Config.mRoomId)||TextUtils.isEmpty(Config.mRole)){
                if(callBack!=null)
                callBack.onFailure("课堂链接错误");
                return;
            }

            CCAtlasClient.getInstance().getRoomMsg(Config.mRoomId, new CCAtlasCallBack<String>() {
                @Override
                public void onSuccess(String des) {
                    try {
                        Config.mRoomDes = parseDes(des);
                        switch (Config.mRole) {
                            case "presenter":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.PRESENTER, Config.mUserId, Config.mRoomId, Config.mRoomDes,template);
//                                ValidateActivity.startSelf(context, mRoomDes.getName(), mRoomDes.getDesc(), mRoomId, mUserId, CCInteractSession.PRESENTER, false);
                                break;
                            case "assistant":
//                                                if (callBack != null) {
//                                                    callBack.onSuccess();
//                                                }
//                                                DirectionActivity.startSelf(context, CCAtlasClient.ASSISTANT, mUserId, mRoomId, mRoomDes);
                                if (callBack != null) {
                                    callBack.onFailure("目前版本不支持助教角色");
                                }
                                break;
                            case "talker":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.TALKER, Config.mUserId, Config.mRoomId, Config.mRoomDes,template);
                                break;
                            case "inspector":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.INSPECTOR, Config.mUserId, Config.mRoomId, Config.mRoomDes,template);
                                break;
                            default:
                                if (callBack != null) {
                                    callBack.onFailure("请使用直播客户端启动");
                                }
                                break;
                        }
                    } catch (Exception e) {
                        if (callBack != null) {
                            callBack.onFailure(e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    if (callBack != null) {
                        callBack.onFailure(errMsg);
                    }
                }
            });
        } catch (Exception e) {
            if (callBack != null) {
                callBack.onFailure("课堂链接错误");
            }
        }
    }

    public static void parseH5Url(final Context context, String url, final ParseCallBack callBack) {
        try {
            if (callBack != null) {
                callBack.onStart();
            }
            if (TextUtils.isEmpty(url)) {
//                throw new NullPointerException("课堂链接错误");
            }
            Log.i(TAG, url);
            Uri uri = Uri.parse(url);
            Config.mUserId = uri.getQueryParameter("userid");
            Config.mRoomId = uri.getQueryParameter("roomid");
            Config.ServerUrl ="class.csslcloud.net";
//            ServerUrl = uri.getQueryParameter("domain");
            Config.mRole = uri.getQueryParameter("role");
            Log.d("SSSSSS", "mUserId+mRoomId+ServerUrl+mRole"+ Config.mUserId+ Config.mRoomId+ Config.ServerUrl+ Config.mRole);
            CCAtlasClient.getInstance().getRoomMsg(Config.mRoomId, new CCAtlasCallBack<String>() {
                @Override
                public void onSuccess(String des) {
                    try {
                        Config.mRoomDes = parseDes(des);
                        switch (Config.mRole) {
                            case "presenter":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.PRESENTER, Config.mUserId, Config.mRoomId, Config.mRoomDes);
//                                ValidateActivity.startSelf(context, mRoomDes.getName(), mRoomDes.getDesc(), mRoomId, mUserId, CCInteractSession.PRESENTER, false);
                                break;
                            case "assistant":
//                                                if (callBack != null) {
//                                                    callBack.onSuccess();
//                                                }
//                                                DirectionActivity.startSelf(context, CCAtlasClient.ASSISTANT, mUserId, mRoomId, mRoomDes);
                                if (callBack != null) {
                                    callBack.onFailure("目前版本不支持助教角色");
                                }
                                break;
                            case "talker":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.TALKER, Config.mUserId, Config.mRoomId, Config.mRoomDes);
                                break;
                            case "inspector":
                                if (callBack != null) {
                                    callBack.onSuccess();
                                }
                                startSelf(context, CCAtlasClient.INSPECTOR, Config.mUserId, Config.mRoomId, Config.mRoomDes);
                                break;
                            default:
                                if (callBack != null) {
                                    callBack.onFailure("请使用直播客户端启动");
                                }
                                break;
                        }
                    } catch (Exception e) {
                        if (callBack != null) {
                            callBack.onFailure(e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(int errCode, String errMsg) {
                    if (callBack != null) {
                        callBack.onFailure(errMsg);
                    }
                }
            });
        } catch (Exception e) {
            if (callBack != null) {
//                callBack.onFailure("课堂链接错误");
            }
        }
    }

    private static RoomDes parseDes(String des) throws JSONException {
        JSONObject obj = new JSONObject(des);
        int videoMode = obj.getInt("video_mode");
        int platform = obj.getInt("platform");
        String followid = obj.getString("is_follow");
        String desc = obj.getString("desc");
        String userid = obj.getString("userid");
        int classType = obj.getInt("classtype");
        int roomType = obj.getInt("room_type");
        int presenterBitrate = obj.getInt("publisher_bitrate");
        int templateType = obj.getInt("templatetype");
        String name = obj.getString("name");
        int maxUsers = obj.getInt("max_users");
        int talkerBitrate = obj.getInt("talker_bitrate");
        int maxStreams = obj.getInt("max_streams");
        int authType = obj.getInt("authtype");
        int inspector_authtype = obj.getInt("inspector_authtype");
        int audience_authtype = obj.getInt("audience_authtype");
        int talker_authtype = obj.getInt("talker_authtype");
        int layoutMode = obj.optInt("layout_mode");

        RoomDes roomDes = new RoomDes();
        roomDes.setVideoMode(videoMode);
        roomDes.setPlatform(platform);
        roomDes.setFollowid(followid);
        roomDes.setDesc(desc);
        roomDes.setUserid(userid);
        roomDes.setClassType(classType);
        roomDes.setRoomType(roomType);
        roomDes.setPresenterBitrate(presenterBitrate);
        roomDes.setTemplateType(templateType);
        roomDes.setName(name);
        roomDes.setMaxUsers(maxUsers);
        roomDes.setTalkerBitrate(talkerBitrate);
        roomDes.setMaxStreams(maxStreams);
        roomDes.setAuthtype(authType);
        roomDes.setInspectorAuthtype(inspector_authtype);
        roomDes.setAudienceAuthtype(audience_authtype);
        roomDes.setTalkerAuthtype(talker_authtype);
        roomDes.setLayoutMode(layoutMode);

        return roomDes;
    }

    public static CCCityListSet parseDispatch(String json)throws JSONException, ApiException {
        Log.i(TAG, "initdispatch: " + json);
        CCCityInteractBean ccCityInteractBean;
        ArrayList<CCCityInteractBean> mCitySet = new ArrayList<>();
        JSONObject object = new JSONObject(json);
        JSONArray data = object.getJSONArray("data");
        String area_code = object.getString("area_code");
        String result = object.getString("result");
        String loc = object.getString("loc");
        for (int i = 0; i < data.length(); i++) {
            ccCityInteractBean = parseDispatchData(data.getJSONObject(i));
            mCitySet.add(ccCityInteractBean);
        }
        CCCityListSet mCityListSet = new CCCityListSet();
        mCityListSet.setareacode(area_code);
        mCityListSet.setloc(loc);
        mCityListSet.setresult(result);
        mCityListSet.setBaseJson(json);
        mCityListSet.setLiveListSet(mCitySet);

        return mCityListSet;
    }

    private static CCCityInteractBean parseDispatchData(JSONObject docInfoObj)throws JSONException, ApiException{
        Log.i(TAG, "initdispatch: " + docInfoObj);
        CCCityInteractBean ccCityInteractBean = new CCCityInteractBean();
        String data_areacode = docInfoObj.getString("area_code");
        String data_loc = docInfoObj.getString("loc");
        ccCityInteractBean.setdataareacode(data_areacode);
        ccCityInteractBean.setdataloc(data_loc);
        return ccCityInteractBean;
    }



    public interface ParseCallBack {
        void onStart();

        void onSuccess();

        void onFailure(String err);
    }

}
