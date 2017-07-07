package com.ride.snailplayer.framework.ui.me;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityMeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItem;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItems;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentStatePagerItemAdapter;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.info.UserInfoActivity;
import com.ride.snailplayer.framework.ui.info.event.OnUserInfoUpdateEvent;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.fragment.AboutMeFragement;
import com.ride.snailplayer.framework.ui.me.fragment.AttentionFragment;
import com.ride.snailplayer.framework.ui.me.viewmodel.MeViewModel;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.widget.GradientTextView;
import com.ride.util.common.util.BarUtils;
import com.ride.util.common.util.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MeActivity extends BaseActivity {

    private ActivityMeBinding mBinding;
    private FragmentPagerItems mItems;
    private FragmentStatePagerItemAdapter mAdapter;
    private MeViewModel mMeViewModel;
    private UserViewModel mUserViewModel;

    private boolean mIsTabClicked;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, MeActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_me);
        mBinding.setMeActionHandler(this);
        mMeViewModel = ViewModelProviders.of(this).get(MeViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        EventBus.getDefault().register(this);

        initAppBar();
        initTabLayout();
    }

    private void initAppBar() {
        setupUserInfo();

        //设置Appbar渐变效果
        mBinding.appbarMe.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.action_bar_size);
            int collapsingLayoutMaxOffset = getResources().getDimensionPixelSize(R.dimen.me_header_height)
                    - actionBarHeight - BarUtils.getStatusBarHeight(MeActivity.this);
            int titleAutoTransparentTotalOffset = 2 * actionBarHeight;
            int titleAutoTransparentMinOffset = collapsingLayoutMaxOffset - titleAutoTransparentTotalOffset;

            int appbarContentAutoTransparentTotalOffset = (int) (1.5 * actionBarHeight);
            int appbarContentAutoTransparentMaxOffset = collapsingLayoutMaxOffset - appbarContentAutoTransparentTotalOffset;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //渐变Toolbar Title
                if (Math.abs(verticalOffset) >= titleAutoTransparentMinOffset) {
                    float fraction = (Math.abs(verticalOffset) - titleAutoTransparentMinOffset) / (float) (titleAutoTransparentTotalOffset);
                    mBinding.tvMeTitle.setAlpha(fraction);
                } else {
                    mBinding.tvMeTitle.setAlpha(0f);
                }

                //渐变Appbar内容区域：头像等
                if (Math.abs(verticalOffset) >= appbarContentAutoTransparentMaxOffset) {
                    mBinding.circleIvMeAvatar.setAlpha(0f);
                    mBinding.tvMeUserName.setAlpha(0f);
                    mBinding.llMeMyInfo.setAlpha(0f);
                } else {
                    float fraction = (appbarContentAutoTransparentMaxOffset - Math.abs(verticalOffset)) / (float) (appbarContentAutoTransparentTotalOffset);
                    mBinding.circleIvMeAvatar.setAlpha(fraction);
                    mBinding.tvMeUserName.setAlpha(fraction);
                    mBinding.llMeMyInfo.setAlpha(fraction);
                }
            }
        });
    }

    private void setupUserInfo() {
        User user = mUserViewModel.getUser();
        if (user != null) {
            Glide.with(this).load(user.getAvatarUrl()).dontAnimate().into(mBinding.circleIvMeAvatar);
            mBinding.setUser(user);
        } else {
            Glide.with(this).load(R.drawable.default_profile).dontAnimate().into(mBinding.circleIvMeAvatar);
        }
    }

    private void initTabLayout() {
        mItems = FragmentPagerItems.with(this).create();
        mAdapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), mItems);
        mBinding.viewPagerMe.setAdapter(mAdapter);

        mBinding.smartTabLayoutMe.setCustomTabView((container, position, adapter) -> {
            GradientTextView view = new GradientTextView(container.getContext());
            view.setText((String) adapter.getPageTitle(position));
            view.setTextSize(ScreenUtils.sp2px(14));
            view.setSourceTextColor(ContextCompat.getColor(this, R.color.app_secondary_text));
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
        mBinding.smartTabLayoutMe.setOnTabClickListener(position -> {
            mIsTabClicked = true;
            changeTab(position);
        });
        mBinding.smartTabLayoutMe.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0 && !mIsTabClicked) {
                    //渐变tab颜色
                    GradientTextView left = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position);
                    GradientTextView right = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position + 1);
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

        //初始化ViewPager
        mItems.add(FragmentPagerItem.of(getResources().getString(R.string.attention_frag_title), AttentionFragment.class));
        mItems.add(FragmentPagerItem.of(getResources().getString(R.string.about_me_frag_title), AboutMeFragement.class));
        mAdapter.notifyDataSetChanged();
        mBinding.smartTabLayoutMe.setViewPager(mBinding.viewPagerMe);
        changeTab(0);
    }

    private void changeTab(int position) {
        GradientTextView selected = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position);
        selected.setAlphaRatio(1f);

        //reset其他tab
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (i != position) {
                GradientTextView tab = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(i);
                tab.setAlphaRatio(0f);
            }
        }
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.iv_me_back:
                onBackPressed();
                break;
            case R.id.circle_iv_me_avatar:
                AvatarActivity.launchActivity(this, mBinding.circleIvMeAvatar, getResources().getString(R.string.transition_avatar));
                break;
            case R.id.ll_me_my_info:
                UserInfoActivity.launchActivity(this);
                break;
        }
    }

    @Subscribe
    public void onAvatarChange(OnAvatarChangeEvent event) {
        setupUserInfo();
    }

    @Subscribe
    public void onUserInfoUpdated(OnUserInfoUpdateEvent event) {
        setupUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
