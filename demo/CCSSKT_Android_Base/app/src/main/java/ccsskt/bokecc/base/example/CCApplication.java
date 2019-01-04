package ccsskt.bokecc.base.example;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;

import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.CCInteractSDK;
import com.bokecc.sskt.base.MyBroadcastReceiver;
//import com.tencent.bugly.crashreport.CrashReport;

import java.lang.ref.WeakReference;

/**
 * 作者 ${CC视频}.<br/>
 */
public class CCApplication extends Application {

    private static final String TAG = "CCApp";

    public static int mAppStatus = -1; // 表示 force_kill
    private static WeakReference<Context> context;
    public AudioManager audioManager;
    @Override
    public void onCreate() {
        super.onCreate();

//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        audioManager.setSpeakerphoneOn(true);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        MyBroadcastReceiver.getInstance().initial(this.getApplicationContext(),audioManager);
        CCInteractSDK.init(this.getApplicationContext(), true);
        if (context == null) {
            context = new WeakReference<Context>(this);
        }
        CCInteractSDK.getInstance().setCrashListen(this.getApplicationContext(),null);
    }

    public static Context getContext() {
        return context == null ? null : context.get();
    }

    public static String getVersion() {
        try {
            PackageManager manager = context.get().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.get().getPackageName(), 0);
            return "v" + info.versionName;
        } catch (Exception e) {
            return "v" + Config.APP_VERSION;
        }
    }

}
