package com.ride.snailplayer.framework.ui.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItem;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItems;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentStatePagerItemAdapter;
import com.ride.snailplayer.framework.ui.home.fragment.list.MovieListFragment;
import com.ride.snailplayer.framework.ui.home.fragment.recommend.RecommendFragment;
import com.ride.snailplayer.net.model.Channel;
import com.ride.snailplayer.widget.GradientTextView;
import com.ride.util.common.util.ScreenUtils;

import java.util.List;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private LiveData<List<Channel>> mPreloadChannelList;
    private FragmentStatePagerItemAdapter mAdapter;
    private FragmentPagerItems mItems = FragmentPagerItems.with(this).create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mBinding.setHomeActionHandler(this);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        setupTab();
    }

    private void setupTab() {
        mItems = FragmentPagerItems.with(this).create();
        mAdapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), mItems);
        mBinding.homeViewPager.setAdapter(mAdapter);

        mBinding.homeSmartTabLayout.setCustomTabView((container, position, adapter) -> {
            GradientTextView view = new GradientTextView(container.getContext());
            view.setText((String) adapter.getPageTitle(position));
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            int padding = ScreenUtils.dp2px(16);
            view.setPadding(padding, padding, padding, padding);
            return view;
        });
        mBinding.homeSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0) {
                    //渐变tab颜色
                    GradientTextView left = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position);
                    GradientTextView right = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position + 1);
                    left.setAlphaRatio(1 - positionOffset);
                    right.setAlphaRatio(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                changeTab((GradientTextView) mBinding.homeSmartTabLayout.getTabAt(position), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
                changeTab((GradientTextView) mBinding.homeSmartTabLayout.getTabAt(0), 0);
            }
        });
    }

    private void changeTab(GradientTextView selectedTab, int position) {
        selectedTab.setAlphaRatio(1f);

        //reset其他tab
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (i != position) {
                GradientTextView tab = (GradientTextView) mBinding.homeSmartTabLayout.getTabAt(i);
                tab.setAlphaRatio(0f);
            }
        }
    }

    public void onMenuAllChannelClick() {

    }

    public void onMenuSearchClick() {

    }

    public void onMenuFileDownloadClick() {

    }
}
