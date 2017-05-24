package com.ride.util.common;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author Stormouble
 * @since 2016/12/25.
 */

public final class ScreenUtils {

    private ScreenUtils() {
        throw new UnsupportedOperationException("ScreenUtils can't be instantiated");
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕密度
     */
    public static int getScreenDensityDpi() {
        WindowManager wm = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.densityDpi;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 将dp转换为px
     */
    public static int dp2px(float dp) {
        float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * 将px转换为dp
     */
    public static int px2dp( @FloatRange(from = 0) float px) {
        float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

}
