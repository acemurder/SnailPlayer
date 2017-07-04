package com.ride.snailplayer.framework.ui.register.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostActivity = (RegisterActivity) mActivity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHostActivity = null;
    }
}
