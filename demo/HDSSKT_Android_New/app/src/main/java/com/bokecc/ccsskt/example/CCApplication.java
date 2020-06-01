package com.bokecc.ccsskt.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.common.util.CCInteractSDK;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局入口类
 * 作者 ${CC视频}.<br/>
 * @Modify wy
 */
public class CCApplication extends MultiDexApplication {

    private static final String TAG = "CCApplication";

    /**Application的context*/
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    /**课堂模式（方向）0竖屏 1横屏*/
    public static int sClassDirection = 0;
    public static String mFisrtCityName;//城市节点的名称
    public static String mAreaCode;//城市的区域。

    public static boolean updataState = false;//判断网络连接
    public static CopyOnWriteArrayList<String> remindStrings = new CopyOnWriteArrayList<>();

    private int mCount;

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //初始化CCSDK，不可多次调用
        CCInteractSDK.init(this, true);

        //初始化bugly
        CrashReport.initCrashReport(this, BuildConfig.buglyKey, true);

        //注册生命周期回调
//        registerActivityLifecycle();
    }

    /**
     * 注册生命周期回调
     */
    /*private void registerActivityLifecycle(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.i(TAG, "onActivityCreated: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i(TAG, "onActivityStarted: " + activity.getLocalClassName());
                if (mCount == 0) {
                    CCAtlasClient.getInstance().setSubStreamAudio(true);
                }
                mCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i(TAG, "onActivityResumed: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(TAG, "onActivityPaused: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i(TAG, "onActivityStopped: " + activity.getLocalClassName());
                mCount--;
                if (mCount == 0) {
                    CCAtlasClient.getInstance().setSubStreamAudio(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i(TAG, "onActivitySaveInstanceState: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i(TAG, "onActivityDestroyed: " + activity.getLocalClassName());
            }

        });
    }*/

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        CCInteractSDK.getInstance().onTerminate();
    }

}
