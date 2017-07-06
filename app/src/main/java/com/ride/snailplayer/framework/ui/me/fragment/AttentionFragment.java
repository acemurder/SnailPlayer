package com.ride.snailplayer.framework.ui.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.databinding.FragmentAttentionBinding;
import com.ride.snailplayer.framework.base.BaseFragment;

/**
 * @author Stormouble
 * @since 2017/7/6.
 */

public class AttentionFragment extends BaseFragment {

    private FragmentAttentionBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentAttentionBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

}
