package com.ride.snailplayer.framework.ui.welcome;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityWelcomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;

import java.util.List;

/**
 * @author Stormouble
 * @since 2017/6/1.
 */

public class WelcomeActivity extends BaseActivity {

    private ActivityWelcomeBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
    }

    /**
     * Tracks whether to display this activity.
     *
     * @param context the application context.
     * @return true if the activity should be displayed, otherwise false.
     */
    public static boolean shouldDisplay(Context context) {
        return false;
    }

    /**
     * Gets the current fragment to display.
     *
     * @param context the application context.
     * @return the fragment to display, or null if there is no fragment.
     */
    private static WelcomeFragment getCurrentFragment(Context context) {
        List<WelcomeFragment> welcomeActivityContents = getWelcomeFragments();

        for (WelcomeFragment fragment : welcomeActivityContents) {
            if (fragment.shouldDisplay(context)) {
                return fragment;
            }
        }

        return null;
    }

    /**
     * Returns all fragments displayed by {@link WelcomeActivity}.
     */
    private static List<WelcomeFragment> getWelcomeFragments() {
        return null;
    }
}
