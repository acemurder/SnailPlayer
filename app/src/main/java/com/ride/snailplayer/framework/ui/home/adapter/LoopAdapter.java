package com.ride.snailplayer.framework.ui.home.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.util.log.Timber;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/2
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class LoopAdapter extends LoopPagerAdapter {
    private List<VideoInfo> videoInfoList;
    public LoopAdapter(RollPagerView viewPager,List<VideoInfo> videoInfos) {
        super(viewPager);
        videoInfoList = videoInfos;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        Timber.e(videoInfoList.get(position).shortTitle);
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(container.getContext()).load(videoInfoList.get(position).img).into(view);
        return view;
    }

    @Override
    public int getRealCount() {
        return videoInfoList.size();
    }
}
