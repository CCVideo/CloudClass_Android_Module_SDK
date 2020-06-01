package com.bokecc.room.ui.utils;

import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.PicToken;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.network.OkhttpNet.OKHttpStatusListener;
import com.bokecc.sskt.base.common.network.OkhttpNet.OKHttpUtil;
import com.bokecc.sskt.base.common.util.CCInteractSDK;
import com.example.ccchatlibrary.CCChatManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author swh
 * @Description 发送请求相关
 */
public class RequestUtil {
    /**
     * 获取图片上传的token
     * @param mCCAtlasClient mCCAtlasClient
     * @param file file
     */
    public static void getUpdatePicToken(CCAtlasClient mCCAtlasClient,final CCChatManager mCCChatManager, final File file){
        mCCAtlasClient.getPicUploadToken(new CCAtlasCallBack<PicToken>() {
            @Override
            public void onSuccess(PicToken picToken) {
                // 开始上传 demo 仅提供思路
                updatePic(mCCChatManager,file, picToken);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.showToast(errMsg);
            }
        });
    }

    /**
     * 上传图片
     * @param mCCChatManager
     * @param file
     * @param picToken
     */
    private static void updatePic(final CCChatManager mCCChatManager, File file, final PicToken picToken) {
        Map<String, String> parms = new HashMap<>();
        parms.put("OSSAccessKeyId", picToken.getAccessid());
        parms.put("policy", picToken.getPolicy());
        parms.put("signature", picToken.getSignature());
        long time = System.currentTimeMillis();
        final String key = picToken.getDir() + "/" + time + "_android.png";
        parms.put("key", key);
        parms.put("success_action_status", "200");
        OKHttpUtil.updateFile(CCInteractSDK.getInstance().getContext(), picToken.getHost(), file, parms, new OKHttpStatusListener() {
            @Override
            public void onSuccessed(String result) {
                mCCChatManager.sendPic(picToken.getHost() + "/" + key);
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                Tools.showToast(errorMsg);
            }
        });
    }
}
