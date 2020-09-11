package com.bokecc.ccsskt.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.bokecc.cloudclass.demo.activity.HomeActivity;

/**
 * 闪屏界面
 * @author wy
 */
public class SplashActivity extends Activity {

    private final long SPLASH_DELAY = 2 * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //延迟2s进入
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }

}
