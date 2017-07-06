package com.ride.util.common.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * @author Stormouble
 * @since 2017/7/5.
 */

public class BarUtils {

    private BarUtils() {
        throw new UnsupportedOperationException("BarUtils can't be instantiated");
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(@NonNull final Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return ScreenUtils.dp2px(25);
        }
    }

    /**
     * 获取Toolbar高度
     *
     * @param context context
     * @return Toolbar高度
     */
    public static int getActionBarHeight(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, outValue, true);
            if (outValue.resourceId > 0) {
                return context.getResources().getDimensionPixelSize(outValue.resourceId);
            } else {
                return ScreenUtils.dp2px(56);
            }
        } else {
            return 0;
        }
    }

}
