package com.ride.snailplayer.framework.base;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * @author Stormouble
 * @since 2017/5/24.
 */
public class BaseFragment extends Fragment implements LifecycleOwner {

    protected Activity mActivity;

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
