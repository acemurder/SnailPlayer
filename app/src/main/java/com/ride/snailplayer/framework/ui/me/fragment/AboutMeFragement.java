package com.ride.snailplayer.framework.ui.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.databinding.FragmentAboutMeBinding;
import com.ride.snailplayer.framework.base.BaseFragment;

/**
 * @author Stormouble
 * @since 2017/7/6.
 */

public class AboutMeFragement extends BaseFragment {

    private FragmentAboutMeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentAboutMeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

}
