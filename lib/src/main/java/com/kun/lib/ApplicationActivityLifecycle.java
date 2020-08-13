package com.kun.lib;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;

import com.kun.lib.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

/**
 * Author: liukun on 2020/8/12.
 * Mail  : 3266817262@qq.com
 * Description:
 */
public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private Observable mObservable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

    public ApplicationActivityLifecycle(Observable observable) {
        mObservable = observable;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        SkinThemeUtils.updateStatusBarColor(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        // 使用Factory2，设置布局加载工厂
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);

        // 这样的做法，不能满足Android Q，即9.0+或者10.0，最多只能满足到Android28，即Android P，即9.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
                //如设置过抛出一次
                //设置 mFactorySet 标签为false
                Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
                field.setAccessible(true);
                field.setBoolean(layoutInflater, false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ApplicationLifecycle", e.toString());
            }
            LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        } else {
            forceSetFactory2(layoutInflater, skinLayoutInflaterFactory);
        }
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
        mObservable.addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

        SkinLayoutInflaterFactory remove = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(remove);
    }

    /**
     * 自定义一个LayoutInflaterCompat的forceSetFactory2方法
     * LayoutInflaterCompat针对Factory2的做法，就是直接修改mFactory2的值
     * https://blog.csdn.net/qq_25412055/article/details/100033637
     * @param inflater
     * @param factory
     */
    private static void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        Class<LayoutInflaterCompat> compatClass = LayoutInflaterCompat.class;
        Class<LayoutInflater> inflaterClass = LayoutInflater.class;
        try {
            Field sCheckedField = compatClass.getDeclaredField("sCheckedField");
            sCheckedField.setAccessible(true);
            sCheckedField.setBoolean(inflater, false);
//            Field mFactory = inflaterClass.getDeclaredField("mFactory");
//            mFactory.setAccessible(true);
            Field mFactory2 = inflaterClass.getDeclaredField("mFactory2");
            mFactory2.setAccessible(true);
//            BackgroundFactory factory = new BackgroundFactory();
//            if (inflater.getFactory2() != null) {
//                factory.setInterceptFactory2(inflater.getFactory2());
//            } else if (inflater.getFactory() != null) {
//                factory.setInterceptFactory(inflater.getFactory());
//            }
            mFactory2.set(inflater, factory);
//            mFactory.set(inflater, factory);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
