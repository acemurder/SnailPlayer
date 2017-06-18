package com.ride.snailplayer.framework.ui.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.ride.snailplayer.R;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.util.SettingUtils;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

/**
 * @author Stormouble
 * @since 2017/6/1.
 */

public class IntroActivity extends MaterialIntroActivity {

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, IntroActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    /**
     * Tracks whether to display this activity.
     *
     * @return true if the activity should be displayed, otherwise false.
     */
    public static boolean shouldDisplay() {
        return !SettingUtils.isAlreadyDisplayIntro();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.theme_primary)
                        .buttonsColor(R.color.theme_accent)
                        .image(R.drawable.img_intro_1)
                        .title(getResources().getString(R.string.app_name))
                        .description(getResources().getString(R.string.app_description_2))
                        .build(), null);
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.theme_primary)
                        .buttonsColor(R.color.theme_accent)
                        .image(R.drawable.img_intro_3)
                        .title(getResources().getString(R.string.app_name))
                        .description(getResources().getString(R.string.app_description_2))
                        .build(), null);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        SettingUtils.markAlreadyDisplayIntro(true);
        HomeActivity.launchActivity(this);
    }
}
