package com.kun.lib;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.kun.lib.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * Author: liukun on 2020/8/12.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class SkinManager extends Observable {

    public volatile static SkinManager instance;
    private Context mContext;

    private ApplicationActivityLifecycle skinActivityLifecycle;

    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance() {
        return instance;
    }

    public SkinManager(Application application) {
        mContext = application;
        SkinPreference.init(application);
        SkinResources.init(application);
        skinActivityLifecycle = new ApplicationActivityLifecycle(this);
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle);
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public void loadSkin(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            SkinPreference.instance.reset();
            SkinResources.getInstance().reset();
        } else {
            try {
                Resources appResource = mContext.getResources();

                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinPath);

                Resources skinResources = new Resources(assetManager, appResource.getDisplayMetrics(), appResource.getConfiguration());

                PackageManager mPm = mContext.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResources, packageName);

                SkinPreference.getInstance().setSkin(skinPath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setChanged();
        notifyObservers(null);
    }
}
