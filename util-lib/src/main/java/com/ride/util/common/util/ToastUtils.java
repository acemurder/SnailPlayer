package com.ride.util.common.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Stormouble
 * @since 2016/12/20.
 */

public final class ToastUtils {

    private static Toast mToast;

    private ToastUtils() {
        throw new UnsupportedOperationException("ToastUtils can't be instantiated");
    }

    /**
     * 显示Toast
     *
     * @param message Toast的内容
     */
    public static void showShortToast(@NonNull Context context, String message) {
        if (!TextUtils.isEmpty(message)) {
            showToast(context, message, Toast.LENGTH_SHORT);
        }
    }

    private static void showToast(@NonNull Context context, String message, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }
}
