package com.ride.snailplayer.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.DialogCommonBinding;
import com.ride.util.common.log.Timber;

/**
 * @author Stormouble
 * @since 2017/7/3.
 */

public class ErrorDialog extends BaseDialog{

    public ErrorDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog initNetworkDialog(String title) {
        return initSingleButtonDialog(title, mContext.getResources().getString(R.string.network_error));
    }

    public BaseDialog initSingleButtonDialog(String title, String content) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Timber.d("dialog title=" + title + ", content=" + content);
            return null;
        }

        DialogCommonBinding binding = DialogCommonBinding.inflate(LayoutInflater.from(mContext));
        binding.setIsSingleChoice(true);
        binding.setTitle(title);
        binding.setContent(content);
        binding.setListener(view -> {
            int id = view.getId();
            switch (id) {
                case R.id.tv_common_dialog_single:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    break;
            }
        });
        MaterialDialog dialog =  new MaterialDialog.Builder(mContext)
                .customView(binding.getRoot(), false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .build();
        setDialog(dialog);

        return this;
    }
}
