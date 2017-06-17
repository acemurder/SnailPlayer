package com.ride.snailplayer.framework.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityRegisterBinding;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.util.ActivityUtils;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;

public class RegisterActivity extends AppCompatActivity {

    public static final int STEP_FIRST = 1;
    public static final int STEP_SECOND = 2;
    public static final int STEP_THIRD = 3;

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
        mBinding.setRegisterActionHandler(this);

        if (savedInstanceState == null) {
            InputPhoneNumberFragment fragment = InputPhoneNumberFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.register_container);
        }
    }

    public void processBack() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    public void processIndicatorView(final int step) {
        int count = mBinding.llRegisterIndicatorLayout.getChildCount();
        if (step > count) {
            return;
        }

        for (int i = 0; i < step; i++) {
            View view = mBinding.llRegisterIndicatorLayout.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }
        for (int i = step; i < count; i++) {
            View view = mBinding.llRegisterIndicatorLayout.getChildAt(i);
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        processBack();
    }
}
