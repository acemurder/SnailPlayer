package com.ride.snailplayer.framework.ui.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.snailplayer.framework.ui.home.HomeActivity;

/**
 * @author Stormouble
 * @since 2017/6/1.
 */

public abstract class WelcomeFragment extends BaseFragment {

    public abstract boolean shouldDisplay(Context context);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Proceed to the next activity.
     */
    protected void doNext() {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        mActivity.finish();
    }
}
