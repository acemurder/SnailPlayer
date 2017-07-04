package com.ride.snailplayer.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Stormouble
 * @since 2017/7/4.
 */

public class BaseDialog {

    protected Context mContext;
    protected MaterialDialog mDialog;

    public BaseDialog(@NonNull Context context) {
        mContext = context;
    }

    public MaterialDialog getDialog() {
        return mDialog;
    }

    public void setDialog(MaterialDialog dialog) {
        mDialog = dialog;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
