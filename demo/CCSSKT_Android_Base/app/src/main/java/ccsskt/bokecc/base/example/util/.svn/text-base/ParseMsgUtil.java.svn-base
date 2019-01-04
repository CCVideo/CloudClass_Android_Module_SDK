package ccsskt.bokecc.base.example.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.net.EasyCall;
import com.bokecc.sskt.base.net.EasyCallback;
import com.bokecc.sskt.base.net.EasyOKHttp;
import com.bokecc.sskt.base.net.EasyOptions;
import com.bokecc.sskt.base.net.EasyResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ccsskt.bokecc.base.example.ValidateActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static ccsskt.bokecc.base.example.Config.mRole;
import static ccsskt.bokecc.base.example.Config.mRoomId;
import static ccsskt.bokecc.base.example.Config.mUserAccount;
import static ccsskt.bokecc.base.example.Config.ServerUrl;
/**
 * 作者 ${CC视频}.<br/>
 */

public class ParseMsgUtil {
    private EasyOKHttp mEasyOKHttp;

    private ParseMsgUtil() {

    }

    public static void parseUrl(final Context context, final String url, final ParseCallBack callBack) {
        try {
            if (callBack != null) {
                callBack.onStart();
            }
            if (TextUtils.isEmpty(url)) {
                throw new NullPointerException("课堂链接错误");
            }
            Log.i(TAG, url);

            Request.Builder requstUrl = new Request.Builder().url(url);
            requstUrl.method("GET",null);
            Request request = requstUrl.build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Call mcall = okHttpClient.newCall(request);
            mcall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "wdh-->onResponse: " + response.request().url());
                    HttpUrl requestUrl = response.request().url();
                    String mUrl = requestUrl.toString();
                    String arr[] = url.split("\\?|&");
                    mRoomId = arr[1].split("=")[1];
                    mUserAccount = arr[2].split("=")[1];
                    String hosts[] = (arr[0].substring("https://".length(), arr[0].length())).split("/");
                    mRole = hosts[hosts.length - 1];
                    mRole = mRole.substring(0, mRole.length());
                    ServerUrl = hosts[hosts.length - 3];
                    Log.i(TAG, "wdh-->parseUrl: " + mRoomId + " - " + mUserAccount + " - " + ServerUrl);
                    switch (mRole) {
                        case "presenter":
                        case "talker":
                            if (callBack != null) {
                                callBack.onSuccess();
                            }
                            ValidateActivity.startSelf(context, mRoomId, mUserAccount, mRole);
                            break;
                        default:
                            if (callBack != null) {
                                callBack.onFailure("请使用直播客户端启动");
                            }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "parseUrl: [ " + e.getMessage() + " ]");
            if (callBack != null) {
                callBack.onFailure("课堂链接错误");
            }
        }
    }

    public interface ParseCallBack{
        void onStart();
        void onSuccess();
        void onFailure(String err);
    }

}
