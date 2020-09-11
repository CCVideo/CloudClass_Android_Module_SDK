package com.bokecc.ccsskt.example;

import android.support.multidex.MultiDexApplication;

import com.bokecc.cloudclass.demo.CCClassApplication;

/**
 * Application
 */
public class CCApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化CCSDK，不可多次调用
        CCClassApplication.getInstance().init(this);
    }

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        CCClassApplication.getInstance().onTerminate();
    }

}
