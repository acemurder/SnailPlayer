package com.ride.snailplayer.framework.ui.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ride.snailplayer.net.model.Channel;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */
public class HomePagerAdapter  extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private List<Channel> channels;

    public HomePagerAdapter(FragmentManager fm, List<Fragment> fragments, List<Channel> channels) {
        super(fm);
        this.fragments = fragments;
        this.channels = channels;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).name ;
    }
}
