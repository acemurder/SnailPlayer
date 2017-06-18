package com.ride.snailplayer.framework.base;

import android.app.Activity;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.DialogCommonBinding;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.NetworkUtils;


/**
 * @author Stormouble
 * @since 2017/5/19.
 */

public class BaseActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private Toolbar mToolbar;
    private MaterialDialog mProgressDialog;
    private MaterialDialog mErrorDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Timber.w("No view with ID main_content to fade in.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    protected void clear() {
        dismissProgress();
        dismissErrorDialog();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    protected void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .widgetColor(ContextCompat.getColor(this, R.color.theme_accent))
                .progress(true, Integer.MAX_VALUE)
                .content(R.string.loading)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showCommonErrorDialog(String title) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showErrorDialog(title, getResources().getString(R.string.network_error));
        } else {
            showErrorDialog(title, getResources().getString(R.string.app_error));
        }
    }

    protected void showErrorDialog(String title, String content) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }

        DialogCommonBinding binding = DialogCommonBinding.inflate(getLayoutInflater());
        binding.setIsSingleChoice(true);
        binding.setTitle(title);
        binding.setContent(content);
        binding.setListener(view -> {
            int id = view.getId();
            switch (id) {
                case R.id.tv_common_dialog_single:
                    if (mErrorDialog != null && mErrorDialog.isShowing()) {
                        mErrorDialog.dismiss();
                    }
                    break;
            }
        });
        mErrorDialog = new MaterialDialog.Builder(this)
                .customView(binding.getRoot(), false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    protected void dismissErrorDialog() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
        }
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    /**
     * This utility method handles Up navigation intents by searching for a parent activity and
     * navigating there if defined. When using this for an activity make sure to define both the
     * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
     * when the activity has a single parent activity. If the activity doesn't have a single parent
     * activity then don't define one and this method will use back button functionality. If "Up"
     * functionality is still desired for activities without parents then use {@code
     * syntheticParentActivity} to define one dynamically.
     * <p/>
     * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar in
     * Material Design guidelines.
     *
     * @param currentActivity         Activity in use when navigate Up action occurred.
     * @param syntheticParentActivity Parent activity to use when one is not already configured.
     */
    public static void navigateUpOrBack(Activity currentActivity,
                                        Class<? extends Activity> syntheticParentActivity) {
        // Retrieve parent activity from AndroidManifest.
        Intent intent = NavUtils.getParentActivityIntent(currentActivity);

        // Synthesize the parent activity when a natural one doesn't exist.
        if (intent == null && syntheticParentActivity != null) {
            try {
                intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (intent == null) {
            // No parent defined in manifest. This indicates the activity may be used by
            // in multiple flows throughout the app and doesn't have a strict parent. In
            // this case the navigation up button should act in the same manner as the
            // back button. This will result in users being forwarded back to other
            // applications if currentActivity was invoked from another application.
            currentActivity.onBackPressed();
        } else {
            if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
                // Need to synthesize a backstack since currentActivity was probably invoked by a
                // different app. The preserves the "Up" functionality within the app according to
                // the activity hierarchy defined in AndroidManifest.xml via parentActivity
                // attributes.
                TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
            } else {
                // Navigate normally to the manifest defined "Up" activity.
                NavUtils.navigateUpTo(currentActivity, intent);
            }
        }
    }
}
