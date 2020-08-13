package com.kun.lib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.kun.lib.utils.SkinThemeUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * @CreateDate: 2020/7/8 23:36
 * @author: ZZQ
 * @Description: java类作用描述
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {

    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    // 参照LayoutInflater
    // 记录对应View的构造函数的参数
    private static final Class<?>[] mConstructorSignature = new Class[] {Context.class, AttributeSet.class};

    private static final HashMap<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理其，管理当前页面的属性
    private SkinAttribute mSkinAttribute;
    // 用于获取窗口的状态框的信息
    private Activity mActivity;

    public SkinLayoutInflaterFactory(Activity activity) {
        mSkinAttribute = new SkinAttribute();
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //换肤就是在需要时候替换 View的属性(src、background等)
        //所以这里创建 View,从而修改View属性
        View view = createSDKView(name, context, attrs);
        // 这里是因为自定义View没有在createSDKView中创建，所以可以在这里创建
        if (view == null) {
            view = createView(name, context, attrs);
        }
        // 创建出View之后，将View以及其对应的需要换肤的属性和属性值保存
        // 会保存系统View和实现了SkinViewSupport接口的自定义View，没有实现SkinViewSupport接口的自定义View不会保存在SkinAttribute中
        if (view != null) {
            mSkinAttribute.look(view, attrs);
        }
        // 如果是返回的null，则依然是调用系统的LayoutInflater的createView方法创建View，不会影响
        // 这里一定不能返回null，否则依然会去执行系统的onCreateView
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    private View createSDKView(String name, Context context, AttributeSet attrs) {
        // 如果包含. 则不是SDK中的View，而是自定义View，因为自定义View是有包名路径，在xml中
        if (-1 != name.indexOf(".")) {
            return null;
        }
        // 不包含.，则就要在解析的节点name前拼接上比如：android.widget等尝试去反射
        for (int i=0;i<mClassPrefixList.length; i++) {
            View view = createView(mClassPrefixList[i] + name, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            // 通过View的构造器，初始化创建View实例，attrs是View的属性集
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {

        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        // 从缓存中查询构造器，因为有可能有重复的View，在一个布局中，虽然位置不同
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                // 根据Class对象获取Constructor实例
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {

            }
        }
        return constructor;
    }

    @Override
    public void update(Observable o, Object arg) {
        // 通过修改状态栏和导航栏，即StatusBarColor和NavigationBarColor
        SkinThemeUtils.updateStatusBarColor(mActivity);
        mSkinAttribute.applySkin();
    }
}
