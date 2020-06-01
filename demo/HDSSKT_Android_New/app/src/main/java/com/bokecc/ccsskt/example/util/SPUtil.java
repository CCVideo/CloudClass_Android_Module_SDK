package com.bokecc.ccsskt.example.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.global.Config;

/**
 * 作者 ${CC视频}.<br/>
 */

public class SPUtil {

    private SharedPreferences mPreferences;

    private SPUtil() {
        Context context = CCApplication.getContext();
        if (context == null) {
            throw new NullPointerException();
        }
        mPreferences = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtil getIntsance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private static final SPUtil INSTANCE = new SPUtil();
    }

    public void put(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void put(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void put(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void put(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException();
        }
        return mPreferences.getInt(key, defValue);
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long defValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException();
        }
        return mPreferences.getLong(key, defValue);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String delValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException();
        }
        return mPreferences.getString(key, delValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean delValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException();
        }
        return mPreferences.getBoolean(key, delValue);
    }

}
