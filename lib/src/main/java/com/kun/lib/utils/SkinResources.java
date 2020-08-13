package com.kun.lib.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

/**
 * @CreateDate: 2020/7/8 22:16
 * @author: ZZQ
 * @Description: 皮肤资源管理器
 * 保存宿主apk的Resources和皮肤包的Resources
 * 然后实现获取资源的功能
 * 在SkinManager中初始化
 * 而SkinManager在Application中初始化
 */
public class SkinResources {

    private String mSkinPkgName;// 宿主apk的路径
    private boolean isDefaultSkin = true;// 默认是否使用宿主apk

    /**
     * app原始的Resources
     */
    private Resources mAppResources;
    /**
     * 皮肤包Resources
     */
    private Resources mSkinResources;

    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    private static volatile SkinResources instance;

    public static SkinResources getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public void reset() {
        mSkinPkgName = "";
        mSkinResources = null;
        isDefaultSkin = true;
    }

    /**
     * 保存皮肤包的路径和Resources，并且判断是否是使用默认的皮肤
     * @param resources
     * @param pkgName
     */
    public void applySkin(Resources resources, String pkgName) {
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        // 是否使用默认皮肤—只要pkgName为null则使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }

    /**
     * 1.通过原始app中的resId（R.color.XX）获取到自己在apk中resources.arsc中的名字和类型
     * 2.根据名字和类型获取皮肤包中的ID
     * @param resId
     * @return
     */
    public int getIdentifier(int resId) {
        // 如果是使用默认皮肤，则直接返回宿主apk中的resId
        if (isDefaultSkin) {
            return resId;
        }
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        // 根据资源的名称和类型获取皮肤包中对应的资源的id，因为资源的名称和类型可能相同，但是在R文件中的id值可能是不同的
        int skinId = mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
        return skinId;
    }

    /**
     * 输入主APP的resId，找到皮肤包中对应的资源的id，获取对应的颜色值
     * @param resId
     * @return
     */
    public int getColor(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    public Drawable getDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 获取背景，因为背景可能是图片也可能是color
     * 所以需要根据res在resources.arsc中的type来判断
     * @param resId
     * @return
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if ("color".equals(resourceTypeName)) {
            try {
                return getColor(resId);
            }catch (Exception e){
                Log.d("sssss", "getBackground: "+e.toString());
                return resId;
            }

        } else {
            return getDrawable(resId);
        }
    }
}
