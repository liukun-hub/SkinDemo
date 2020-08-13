package com.kun.lib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author: liukun on 2020/8/12.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class SkinPreference {
    public static final String SKIN_SHARE = "skins";
    public static final String KEY_SKIN_PATH = "skin-path";
    private static SharedPreferences mPreferences;

    public volatile static SkinPreference instance;

    private SkinPreference(Context applicationContext) {
        mPreferences = applicationContext.getSharedPreferences(SKIN_SHARE,
                Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinPreference.class) {
                if (instance == null) {
                    instance = new SkinPreference(context.getApplicationContext());
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return instance;
    }

    public void setSkin(String skinPath) {
        mPreferences.edit().putString(KEY_SKIN_PATH, skinPath).apply();
    }

    public void reset() {
        mPreferences.edit().remove(KEY_SKIN_PATH).apply();
    }

    public String getSkin() {
        return mPreferences.getString(KEY_SKIN_PATH, null);
    }

}
