package com.ride.snailplayer.framework.ui.home.adapter;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ItemRecommendLooperViewBinding;
import com.ride.snailplayer.framework.ui.play.PlayActivity;
import com.ride.snailplayer.net.model.VideoInfo;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/18
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class LooperAdapter extends DelegateAdapter.Adapter<LooperAdapter.LooperViewHolder> {
    private List<VideoInfo> videoInfoList;
    private Fragment fragment;


    public LooperAdapter(List<VideoInfo> videoInfoList, Fragment fragment) {
        this.videoInfoList = videoInfoList;
        this.fragment = fragment;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper(1);
    }


    @Override
    public LooperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LooperViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_looper_view, parent, false), videoInfoList);
    }

    @Override
    public void onBindViewHolder(LooperViewHolder holder, int position) {
        holder.itemView.setOnClickListener((v -> PlayActivity.launchActivity(fragment.getActivity(),
                videoInfoList.get(position))));
    }



    @Override
    public int getItemCount() {
        return 1;
    }

    class LooperViewHolder extends RecyclerView.ViewHolder {
        ItemRecommendLooperViewBinding binding;
        LoopAdapter mLoopAdapter;

        public LooperViewHolder(View itemView, List<VideoInfo> videoInfoList) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            mLoopAdapter = new LoopAdapter(binding.loopPagerView, videoInfoList);
            binding.loopPagerView.getViewPager().setPageMargin(20);
            binding.loopPagerView.getViewPager().setOffscreenPageLimit(4);
            RelativeLayout.LayoutParams paramTest = (RelativeLayout.LayoutParams)
                    binding.loopPagerView.getViewPager().getLayoutParams();
            paramTest.leftMargin = 80;
            paramTest.rightMargin = 80;
            binding.loopPagerView.getViewPager().setLayoutParams(paramTest);
            binding.loopPagerView.getViewPager().setClipChildren(false);
            binding.loopPagerView.setAdapter(mLoopAdapter);
            binding.loopPagerView.setHintView(null);
            binding.loopPagerView.setOnItemClickListener((position ->
                    PlayActivity.launchActivity(fragment.getActivity(),videoInfoList.get(position))));

        }


    }
}
