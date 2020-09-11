package com.bokecc.cloudclass.demo.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author 获得视频
 * @Date: on 2019/8/1.
 * @Email: houbs@bokecc.com
 * 重启软件
 */
public class RebootUtils {

    public static void rebootWithValue(Context context, Class<?> cls, int value) {
        Intent mStartActivity = new Intent(context, cls);
        int mPendingIntentId = value;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static void rebootOnly(Activity activity) {
        Intent i = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
    }
}
