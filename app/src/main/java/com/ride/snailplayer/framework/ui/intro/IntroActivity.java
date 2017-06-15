package com.ride.snailplayer.framework.ui.intro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.util.SettingUtils;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

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
                        .possiblePermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS})
                        .image(R.drawable.img_intro_1)
                        .title("title 3")
                        .description("Description 3")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("We provide solutions to make you love your work");
                    }
                }, "Work with love"));
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.theme_primary)
                        .buttonsColor(R.color.theme_accent)
                        .possiblePermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS})
                        .image(R.drawable.img_intro_2)
                        .title("title 3")
                        .description("Description 3")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("We provide solutions to make you love your work");
                    }
                }, "Work with love"));
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.theme_primary)
                        .buttonsColor(R.color.theme_accent)
                        .possiblePermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS})
                        .image(R.drawable.img_intro_3)
                        .title("title 3")
                        .description("Description 3")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("We provide solutions to make you love your work");
                    }
                }, "Work with love"));
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //SettingUtils.markAlreadyDisplayIntro(true);
        HomeActivity.launchActivity(this);
    }
}
