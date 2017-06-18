package com.ride.snailplayer.framework.ui.register.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.DialogCommonBinding;
import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.util.common.util.NetworkUtils;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class BaseRegisterFragment extends BaseFragment {

    protected RegisterActivity mHostActivity;
    protected MaterialDialog mProgressDialog;
    protected MaterialDialog mErrorDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostActivity = (RegisterActivity) mActivity;
    }

    protected void showProgress() {
        mProgressDialog = new MaterialDialog.Builder(getContext())
                .widgetColor(ContextCompat.getColor(getContext(), R.color.theme_accent))
                .progress(true, Integer.MAX_VALUE)
                .content(R.string.loading)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showCommonErrorDialog(String title) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showErrorDialog(title, getResources().getString(R.string.network_error));
        } else {
            showErrorDialog(title, getResources().getString(R.string.app_error));
        }
    }

    protected void showErrorDialog(String title, String content) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }

        DialogCommonBinding binding = DialogCommonBinding.inflate(getActivity().getLayoutInflater());
        binding.setIsSingleChoice(true);
        binding.setTitle(title);
        binding.setContent(content);
        binding.setListener(view -> {
            int id = view.getId();
            switch (id) {
                case R.id.tv_common_dialog_single:
                    if (mErrorDialog != null && mErrorDialog.isShowing()) {
                        mErrorDialog.dismiss();
                    }
                    break;
            }
        });
        mErrorDialog = new MaterialDialog.Builder(getContext())
                .customView(binding.getRoot(), false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    protected void dismissErrorDialog() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgress();
        dismissErrorDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHostActivity = null;
    }
}
