package com.ride.snailplayer.framework.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.ui.adapter.HomePagerAdapter;
import com.ride.snailplayer.framework.ui.fragment.MovieListFragment;
import com.ride.snailplayer.net.model.Channel;
import com.ride.util.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements HomeContract.View {

    private ActivityHomeBinding mBinding;
    private List<Fragment> fragments = new ArrayList<>();
    private HomePagerAdapter adapter;
    private List<Channel> channelList = new ArrayList<>();
    private HomeContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
        presenter = new HomePresenter(this);
        presenter.loadChannels();
    }

    private void initView() {
        adapter = new HomePagerAdapter(getSupportFragmentManager(),fragments,channelList);
        mBinding.homeViewPager.setAdapter(adapter);
        mBinding.homeTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.homeTabLayout.setupWithViewPager(mBinding.homeViewPager);
        mBinding.homeTabLayout.setTabsFromPagerAdapter(adapter);//给Tabs设置适配器
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initFragment(List<Channel> channels) {
        for(Channel c : channels){
            mBinding.homeTabLayout.addTab(mBinding.homeTabLayout.newTab().setText(c.name));
            MovieListFragment fragment = new MovieListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",c.id);
            bundle.putString("name",c.name);
            Timber.i(c.name);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unBind();
    }

    @Override
    public void onSuccess(List<Channel> channelList) {
        this.channelList.addAll(channelList);
        initFragment(channelList);
    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onServerError() {

    }

}
