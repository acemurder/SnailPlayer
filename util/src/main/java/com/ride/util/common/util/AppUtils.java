package com.ride.util.common.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @author Stormouble
 * @since 2017/5/19.
 */

public final class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("AppUtils can't be initialized");
    }

    /**
     * 判断是否是Debug版本
     *
     * @return
     */
    public static boolean isDebug() {
        String packageName = Utils.getContext().getPackageName();
        if (StringUtils.isSpace(packageName)) {
            return false;
        }

        try {
            PackageManager pm = Utils.getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取最大内存
     *
     * @return
     */
    public static long getMemorySize() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * 获取已分配的内存
     *
     * @return
     */
    public static long getAllocatedMemorySize() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * 获取可使用的内存
     *
     * @return
     */
    public static long getAvailableMemorySize() {
        return getMemorySize() - getAllocatedMemorySize();
    }
}
