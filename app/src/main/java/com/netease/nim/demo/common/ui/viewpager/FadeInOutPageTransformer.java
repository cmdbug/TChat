package com.netease.nim.demo.common.ui.viewpager;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;
/**
 * Viewpager 页面切换动画，只支持3.0以上版本
 * <p/>
 * [-∞，-1]完全不可见
 * [-1,  0]从不可见到完全可见
 * [0,1]从完全可见到不可见
 * [1,∞]完全不可见
 * <p/>
 * Created by doc on 15/1/6.
 */
public class FadeInOutPageTransformer implements ViewPager.PageTransformer {

    @SuppressLint("NewApi")
    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {//页码完全不可见
            page.setAlpha(0);
        } else if (position < 0) {
            page.setAlpha(1 + position);
        } else if (position < 1) {
            page.setAlpha(1 - position);
        } else {
            page.setAlpha(0);
        }
    }
}
