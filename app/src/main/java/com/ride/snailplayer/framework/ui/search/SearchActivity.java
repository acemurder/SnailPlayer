package com.ride.snailplayer.framework.ui.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivitySearchBinding;
import com.ride.snailplayer.framework.base.BaseActivity;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding mBinding;

    static void launchActivity(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mBinding.setActionHandler(this);

        setupToolbar();
        setupSearchView();

        doEnterAnim();
    }

    private void setupToolbar() {
        Drawable up = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        Toolbar toolbar = getToolbar();
        toolbar.setNavigationIcon(up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUpOrBack(SearchActivity.this, null);
            }
        });
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onBackPressed() {
        doExitAnim(null);
    }

    private void doEnterAnim() {

    }

    public void doExitAnim(View view) {

    }
}
