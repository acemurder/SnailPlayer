package com.ride.snailplayer.framework.ui.register.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentCompleteRegisterBinding;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class CompleteRegisterFragment extends BaseRegisterFragment {

    private FragmentCompleteRegisterBinding mBinding;

    public static CompleteRegisterFragment newInstance() {
        CompleteRegisterFragment fragment = new CompleteRegisterFragment();
        return fragment;
    }

    public CompleteRegisterFragment() {
        //need empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCompleteRegisterBinding.inflate(inflater, container, false);
        mBinding.setCompleteRegisterActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置Indicator
        mHostActivity.processIndicatorView(RegisterActivity.STEP_FIFTH);

    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                ActivityCompat.finishAfterTransition(mHostActivity);
                break;
            case R.id.btn_fcr_login:
                HomeActivity.launchActivity(getActivity());
                break;
        }
    }
}
