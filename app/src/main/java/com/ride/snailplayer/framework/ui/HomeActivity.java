package com.ride.snailplayer.framework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

    }
}
