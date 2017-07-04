package com.ride.snailplayer.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;

/**
 * @author Stormouble
 * @since 2017/7/3.
 */

public class ProgressDialog extends BaseDialog{

    private MaterialDialog mProgressDialog;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog initProgressDialog() {
        mProgressDialog =  new MaterialDialog.Builder(mContext)
                .widgetColor(ContextCompat.getColor(mContext, R.color.theme_accent))
                .progress(true, Integer.MAX_VALUE)
                .content(R.string.loading)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();
        setDialog(mProgressDialog);

        return this;
    }
}
