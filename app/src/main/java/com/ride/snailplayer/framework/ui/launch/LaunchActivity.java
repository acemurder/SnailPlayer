package com.ride.snailplayer.framework.ui.launch;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityLaunchBinding;
import com.ride.snailplayer.framework.base.BaseActivity;

public class LaunchActivity extends AppCompatActivity {

    private ActivityLaunchBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch);

    }
}
