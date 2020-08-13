package com.kun.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;

/**
 * @CreateDate: 2020/7/8 22:16
 * @author: ZZQ
 * @Description: java类作用描述
 */
public class SkinThemeUtils {

    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {androidx.appcompat.R.attr.colorPrimaryDark};
    private static int[] STATUS_BAR_COLOR_ATTRS = {android.R.attr.statusBarColor,android.R.attr.navigationBarColor};

    /**
     * 获得theme中的属性中定义的资源id
     * 其实就是自定义View的时候，自定义的attrs属性获取
     * @param context
     * @param attrs
     * @return
     */
    public static int[] getResId(Context context, int[] attrs) {
        int[] resIds = new int[attrs.length];
        TypedArray a = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < attrs.length; i++) {
            resIds[i] = a.getResourceId(i, 0);
        }
        a.recycle();
        return resIds;
    }

    /**
     * 修改StatusBar的color和NavigationBarColor
     * @param activity
     */
    public static void updateStatusBarColor(Activity activity) {
        // 如果是小于5.0，则不修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        // 获得statusBarColor与navigationBarColor（状态栏颜色）
        // 当与colorPrimaryDark不同时，以statusBarColor为准
        int[] resIds = getResId(activity, STATUS_BAR_COLOR_ATTRS);
        int statusBarColorResId = resIds[0];
        int navigationBarColorId = resIds[1];

        // 如果直接在style中写入固定颜色值（而不是@color/XXX）获得0
        // 其实就是找到colorPrimaryDark
        if (statusBarColorResId != 0) {
            // 通过statusBarColorResId获取颜色值，然后设置给宿主apk的statusBar
            int color = SkinResources.getInstance().getColor(statusBarColorResId);
            activity.getWindow().setStatusBarColor(color);
        } else {
            // 获得colorPrimaryDark
            int colorPrimaryDarkResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (colorPrimaryDarkResId != 0) {
                int color = SkinResources.getInstance().getColor(colorPrimaryDarkResId);
                activity.getWindow().setStatusBarColor(color);
            }
        }
        if (navigationBarColorId != 0) {
            int color = SkinResources.getInstance().getColor(navigationBarColorId);
            activity.getWindow().setNavigationBarColor(color);
        }
    }
}
