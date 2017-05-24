package com.ride.snailplayer.snailplayer.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.v4.app.Fragment;

/**
 * @author Stormouble
 * @since 2017/5/24.
 */
public class BaseFragment extends Fragment implements LifecycleOwner {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
