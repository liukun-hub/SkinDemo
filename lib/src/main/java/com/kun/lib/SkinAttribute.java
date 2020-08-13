package com.kun.lib;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kun.lib.utils.SkinResources;
import com.kun.lib.utils.SkinThemeUtils;

import androidx.core.view.ViewCompat;


import java.util.ArrayList;
import java.util.List;

/**
 * @CreateDate: 2020/7/8 22:16
 * @author: ZZQ
 * @Description: java类作用描述
 */
public class SkinAttribute {
    private static final List<String> mAttributes = new ArrayList<>();
    // 初始化需要换肤的属性名
    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
    }

    private List<SkinView> mSkinViews = new ArrayList<>();

    /**
     * 在LayoutInflater.Factory2的onCreateView方法中将加载的View和属性集保存在SkinAttribute中
     * @param view
     * @param attrs
     */
    public void look(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPairs = new ArrayList<>();

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            // 获得属性名，textColor/background等
            String attributeName = attrs.getAttributeName(i);
            // 判断View的属性集中的属性名是否是需要换肤的
            if (mAttributes.contains(attributeName)) {
                // 获取对应的属性值
                String attributeValue = attrs.getAttributeValue(i);
                // 比如color，以#开头的固定值，不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                // 以?开头的表示使用属性   ?attr/actionBarSize
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    // 所以需要获取?attr对应的属性的值
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    // 正常以@开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }

                SkinPair skinPair = new SkinPair(attributeName, resId);
                mSkinPairs.add(skinPair);
            }
        }

        // 如果属性集不为null，或者是自定义View
        // 保存系统View和自定义View的属性和其属性值
        if (!mSkinPairs.isEmpty() || view instanceof SkinViewSupport) {
            // 创建需要换肤的View
            SkinView skinView = new SkinView(view, mSkinPairs);
            // 如果选择过皮肤，调用一次applySkin加载皮肤资源
            // 这里的目的就是在选择需要换肤的时候，进入新的页面也是直接换肤
            // 第一次是默认情况，SkinResources中isDefaultSkin = true
            skinView.applySkin();
            // 保存需要换肤的View
            mSkinViews.add(skinView);
        }
    }

    /**
     * 遍历每个View，修改当前页面的每个View的颜色和皮肤
     */
    public void applySkin() {
        for (SkinView skinView: mSkinViews) {
            skinView.applySkin();
        }
    }

    /**
     * 存储View以及其对应的属性和资源id集合
     */
    static class SkinView{
        View view;
        // 这个View的能被换肤的属性与它对应的id集合
        List<SkinPair> mSkinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.mSkinPairs = skinPairs;
        }

        /**
         * 对一个View中的所有属性进行修改
         */
        public void applySkin() {
            // 修改自定义View的对应属性的换肤
            applySkinSupport();
            // 遍历View的属性集合
            for (SkinPair skinPair: mSkinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                // 根据属性名，修改不同的换肤效果
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        // 背景可能是color也可能是Drawable
                        if (background instanceof Integer) {
                            view.setBackgroundColor((Integer) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView)view).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView)view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView)view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    default:
                        break;
                }
                if (left != null || right != null || top != null || bottom != null){
                    ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport)view).applySkin();
            }
        }
    }

    static class SkinPair {
        // 属性名
        String attributeName;
        // 属性对应使用的资源id
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
