package com.ride.snailplayer.snailplayer.ui.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.snailplayer.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
    }
}
