package com.ride.snailplayer.framework.ui.register.fragment;

import android.content.Context;

import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;

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
