package com.ride.snailplayer.framework.ui.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityHomeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.ui.home.adapter.HomePagerAdapter;
import com.ride.snailplayer.framework.ui.home.fragment.list.MovieListFragment;
import com.ride.snailplayer.framework.ui.home.fragment.recommend.RecommendFragment;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.util.log.Timber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding mBinding;
    private List<Fragment> fragments = new ArrayList<>();
    private HomePagerAdapter adapter;
    private List<Channel> channelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
        initData();
    }

    private void initView() {
        adapter = new HomePagerAdapter(getSupportFragmentManager(), fragments, channelList);
        mBinding.homeViewPager.setAdapter(adapter);
        mBinding.homeTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.homeTabLayout.setupWithViewPager(mBinding.homeViewPager);
        mBinding.homeTabLayout.setTabsFromPagerAdapter(adapter);//给Tabs设置适配器
    }

    private void initData() {
        Observable<List<Channel>> observable = Observable.create(new ObservableOnSubscribe<List<Channel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Channel>> e) throws Exception {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("channel.json")));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                reader.close();
                Timber.i(Thread.currentThread().getName());
                List<Channel> channels = new Gson().fromJson(builder.toString(), new TypeToken<List<Channel>>() {
                }.getType());
                e.onNext(channels);
                e.onComplete();
            }
        });
        observable.compose(MainThreadObservableTransformer.instance()).subscribe(new Consumer<List<Channel>>() {
            @Override
            public void accept(@NonNull List<Channel> channels) throws Exception {
                initFragment(channels);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragment(List<Channel> channels) {
        Channel channel = new Channel();
        channel.name = "推荐";
        channel.describe = "推荐";
        channel.id = "0";
        channelList.add(channel);
        mBinding.homeTabLayout.addTab(mBinding.homeTabLayout.newTab().setText(channel.name));
        channelList.addAll(channels);
        fragments.add(new RecommendFragment());
        adapter.notifyDataSetChanged();
        for (Channel c : channels) {
            mBinding.homeTabLayout.addTab(mBinding.homeTabLayout.newTab().setText(c.name));
            MovieListFragment fragment = new MovieListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", c.id);
            bundle.putString("name", c.name);
            Timber.i(c.name);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
