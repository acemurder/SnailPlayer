package com.ride.snailplayer.framework.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityRegisterBinding;

import cn.bmob.v3.BmobUser;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mBinding;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, RegisterActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        processRegister();
    }

    private void processRegister() {
    }
}
