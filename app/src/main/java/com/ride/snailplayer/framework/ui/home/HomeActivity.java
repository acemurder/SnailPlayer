package com.ride.snailplayer.framework.ui.home;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItem;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItems;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentStatePagerItemAdapter;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.home.fragment.list.MovieListFragment;
import com.ride.snailplayer.framework.ui.home.fragment.recommend.RecommendFragment;
import com.ride.snailplayer.framework.ui.login.LoginActivity;
import com.ride.snailplayer.framework.ui.me.MeActivity;
import com.ride.snailplayer.framework.ui.info.event.OnUserInfoUpdateEvent;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.search.SearchActivity;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.snailplayer.widget.GradientTextView;
import com.ride.util.common.AppExecutors;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobUser;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private UserViewModel mUserViewModel;
    private LiveData<List<Channel>> mPreloadChannelList;
    private FragmentStatePagerItemAdapter mAdapter;
    private FragmentPagerItems mItems;
    private boolean mIsTabClicked;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, HomeActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mBinding.setHomeActionHandler(this);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        EventBus.getDefault().register(this);

        setupUserInfo();
        setupTab();
    }

    private void setupUserInfo() {
        User user = mUserViewModel.getUser();
        if (user != null) {
            mUserViewModel.setAvatarForCircleImageView(user.getAvatarUrl())
                    .compose(MainThreadObservableTransformer.instance())
                    .subscribe(bitmap -> {
                        Timber.d("头像url=" + user.getAvatarUrl());
                        mBinding.circleIvHomeAvatar.setImageBitmap(bitmap);
                    }, Timber::e);
            mBinding.homeTvLoginStatus.setText(user.getNickName());
        } else {
            mBinding.circleIvHomeAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_profile));
            mBinding.homeTvLoginStatus.setText(getResources().getString(R.string.no_login));
        }
    }

    private void setupTab() {
        mItems = FragmentPagerItems.with(this).create();
        mAdapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), mItems);
        mBinding.homeViewPager.setAdapter(mAdapter);

        mBinding.homeSmartTabLayout.setCustomTabView((container, position, adapter) -> {
            GradientTextView view = new GradientTextView(container.getContext());
            view.setText((String) adapter.getPageTitle(position));
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

            //设置点击效果
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            view.setBackgroundResource(outValue.resourceId);

            //设置padding
            int horizontalPadding = ScreenUtils.dp2px(10);
            view.setPadding(horizontalPadding, 0, horizontalPadding, 0);
            return view;
        });
        mBinding.homeSmartTabLayout.setOnTabClickListener(position -> {
            mIsTabClicked = true;
            changeTab(position);
        });
        mBinding.homeSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0 && !mIsTabClicked) {
                    //渐变tab颜色
                    GradientTextView left = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position);
                    GradientTextView right = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position + 1);
                    left.setAlphaRatio(1 - positionOffset);
                    right.setAlphaRatio(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mIsTabClicked && state == ViewPager.SCROLL_STATE_IDLE) {
                    mIsTabClicked = false;
                }
            }
        });

        mPreloadChannelList = mHomeViewModel.getPreloadChannelList();
        mPreloadChannelList.observe(this, channels -> {
            Bundle bundle = new Bundle();
            if (channels != null && !channels.isEmpty()) {
                //添加推荐tab页
                Channel recommendChannel = channels.get(0);
                bundle.putString("id", recommendChannel.id);
                bundle.putString("name", recommendChannel.name);
                mItems.add(FragmentPagerItem.of(recommendChannel.name, RecommendFragment.class, bundle));

                //添加其他tab页
                for (int i = 1; i < channels.size(); i++) {
                    Channel channel = channels.get(i);
                    bundle = new Bundle();
                    bundle.putString("id", channel.id);
                    bundle.putString("name", channel.name);
                    mItems.add(FragmentPagerItem.of(channel.name, MovieListFragment.class, bundle));
                }
                mAdapter.notifyDataSetChanged();
                mBinding.homeSmartTabLayout.setViewPager(mBinding.homeViewPager);

                //设置第一个tab为选中状态
                changeTab(0);
            }
        });
    }

    private void changeTab(int position) {
        GradientTextView selected = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position);
        selected.setAlphaRatio(1f);

        //reset其他tab
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (i != position) {
                GradientTextView tab = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(i);
                tab.setAlphaRatio(0f);
            }
        }
    }

    @Subscribe
    public void onAvatarChanged(OnAvatarChangeEvent event) {
        setupUserInfo();
    }

    @Subscribe
    public void onUserInfoUpdated(OnUserInfoUpdateEvent event) {
        setupUserInfo();
    }

    public void onMenuSearchClick() {
        SearchActivity.launchActivity(this);
    }

    public void onMenuFileDownloadClick() {
        //TODO
    }

    public void onAvatarClick() {
        User user = mUserViewModel.getUser();
        if (user == null) {
            LoginActivity.launchActivity(this);
        } else {
            MeActivity.launchActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
