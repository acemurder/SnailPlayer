package com.ride.util.common;

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
    public static void showShortToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            showToast(message, Toast.LENGTH_SHORT);
        }
    }

    private static void showToast(String message, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(Utils.getContext(), message, duration);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }
}
