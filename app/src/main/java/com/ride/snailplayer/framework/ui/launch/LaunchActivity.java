package com.ride.snailplayer.framework.ui.launch;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivitySplashBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.intro.IntroActivity;

public class LaunchActivity extends BaseActivity {

    private ActivitySplashBinding mBinding;
    private Handler mHandler;

    private boolean mShouldDisplayIntro;

    private final Runnable mStartIntroActivityRunnbale = () -> {
        IntroActivity.launchActivity(LaunchActivity.this);
        finish();
    };

    private final Runnable mStartHomeActivityRunnable = () -> {
        HomeActivity.launchActivity(LaunchActivity.this);
        finish();
    };

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, LaunchActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mHandler = new Handler();

        mShouldDisplayIntro = IntroActivity.shouldDisplay();
        if (mShouldDisplayIntro) {
            mHandler.postDelayed(mStartIntroActivityRunnbale, 2000);
        } else {
            mHandler.postDelayed(mStartHomeActivityRunnable, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShouldDisplayIntro) {
            mHandler.removeCallbacks(mStartIntroActivityRunnbale);
        } else {
            mHandler.removeCallbacks(mStartHomeActivityRunnable);
        }
    }
}
