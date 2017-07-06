package com.ride.snailplayer.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.DialogProgressBinding;

/**
 * @author Stormouble
 * @since 2017/7/3.
 */

public class ProgressDialog extends BaseDialog {

    private MaterialDialog mProgressDialog;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog initProgressDialog() {
        DialogProgressBinding binding = DialogProgressBinding.inflate(LayoutInflater.from(mContext));
        mProgressDialog = new MaterialDialog.Builder(mContext)
                .customView(binding.getRoot(), false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();
        setDialog(mProgressDialog);
        return this;
    }
}
