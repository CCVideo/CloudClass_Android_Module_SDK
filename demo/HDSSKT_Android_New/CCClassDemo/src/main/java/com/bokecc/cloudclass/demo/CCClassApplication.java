package com.bokecc.cloudclass.demo;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bokecc.sskt.base.common.util.CCInteractSDK;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局入口类
 * 作者 ${CC视频}.<br/>
 * @Modify wy
 */
public class CCClassApplication {

    /**Application的context*/
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    /**课堂模式（方向）0竖屏 1横屏*/
    public static int sClassDirection = 0;
    public static String mFisrtCityName = "北京";//城市节点的名称
    public static String mAreaCode;//城市的区域。

    public static boolean updataState = false;//判断网络连接
    public static CopyOnWriteArrayList<String> remindStrings = new CopyOnWriteArrayList<>();

    private CCClassApplication(){}

    public static CCClassApplication getInstance(){
        return CCClassApplication.SingletonHolder.instance;
    }

    private static class SingletonHolder{
        public static CCClassApplication instance = new CCClassApplication();
    }

    /**
     * 初始化
     * 使用了context和初始化异常捕获等
     * @param globalContext
     */
    public void init(Context globalContext) {
        this.context = globalContext;
        //初始化CCSDK，不可多次调用
        CCInteractSDK.init(context, true);
    }

    public static Context getContext() {
        return context;
    }


    public void onTerminate() {
        CCInteractSDK.getInstance().onTerminate();
    }

}
