package com.ride.snailplayer.framework.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ItemMovieBinding;
import com.ride.snailplayer.net.model.VideoInfo;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    private List<VideoInfo> videoInfoList;
    ItemMovieBinding mBinding;
    private Fragment fragment;


    public VideoListAdapter(List<VideoInfo> videoInfoList, Fragment fragment) {
        this.videoInfoList = videoInfoList;
        this.fragment = fragment;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_movie, parent, false);
        return new ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mBinding = DataBindingUtil.getBinding(holder.itemView);
        mBinding.setVideo(videoInfoList.get(position));
        String url = getUrl(videoInfoList.get(position).img, holder.iv.getWidth(), holder.iv.getHeight());
        Glide.with(fragment).load(url).into(holder.iv);


    }

    private String getUrl(String oldUrl, int width, int height) {
        int index = oldUrl.lastIndexOf(".");
        String url = oldUrl.substring(0, index) +"_195_260";
        return url + ".jpg";
    }

    @Override
    public int getItemCount() {
        return videoInfoList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            iv = mBinding.movieImage;
            this.itemView = itemView;

        }
    }
}
