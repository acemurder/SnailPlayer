package com.ride.snailplayer.framework.ui.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;

import com.ride.snailplayer.BR;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivitySearchBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItemAdapter;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItems;
import com.ride.snailplayer.framework.ui.home.fragment.list.MovieListFragment;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding mBinding;

    private String mQuery;

    public static void launchActivity(Activity activity) {
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

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        if (query == null) {
            mQuery = "";
        } else {
            mQuery = query;
        }
        if (mBinding.searchView != null) {
            mBinding.searchView.setQuery(mQuery, false);
        }

        doEnterAnim();
        overridePendingTransition(0, 0);
    }

    private void setupToolbar() {
        Drawable up = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        Toolbar toolbar = getToolbar();
        toolbar.setNavigationIcon(up);
        toolbar.setNavigationOnClickListener(v -> navigateUpOrBack(SearchActivity.this, null));
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mBinding.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mBinding.searchView.setIconified(false);
        // Set the query hint.
        mBinding.searchView.setQueryHint("");
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFor(newText);
                return true;
            }
        });
        mBinding.searchView.setOnCloseListener(() -> {
            doExitAnim(null);
            return false;
        });
        if (!TextUtils.isEmpty(mQuery)) {
            mBinding.searchView.setQuery(mQuery, false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                searchFor(query);
                mBinding.searchView.setQuery(query, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        doExitAnim(null);
    }

    private void doEnterAnim() {
        mBinding.scrim.animate()
                .alpha(1)
                .setDuration(500L)
                .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        mBinding.cardSearchPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBinding.cardSearchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                // As the height will change once the initial suggestions are delivered by the
                // loader, we can't use the search panels height to calculate the final radius
                // so we fall back to it's parent to be safe
                final ViewGroup searchPanelParent = (ViewGroup) mBinding.cardSearchPanel.getParent();
                final int revealRadius = (int) Math.hypot(
                        searchPanelParent.getWidth(), searchPanelParent.getHeight());
                // Center the animation on the top right of the panel i.e. near to the
                // search button which launched this screen.
                Animator show = ViewAnimationUtils.createCircularReveal(mBinding.cardSearchPanel,
                        mBinding.cardSearchPanel.getRight(), mBinding.cardSearchPanel.getTop(), 0f, revealRadius);
                show.setDuration(250L);
                show.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                        android.R.interpolator.fast_out_slow_in));
                show.start();
                return false;
            }
        });
    }

    public void doExitAnim(View view) {
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        final int revealRadius = (int) Math.hypot(mBinding.cardSearchPanel.getWidth(),
                mBinding.cardSearchPanel.getHeight());
        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(mBinding.cardSearchPanel,
                mBinding.cardSearchPanel.getRight(), mBinding.cardSearchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(200L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                android.R.interpolator.fast_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBinding.cardSearchPanel.setVisibility(View.INVISIBLE);
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        mBinding.scrim.animate()
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                .start();
    }

    private void searchFor(String query) {

    }
}
