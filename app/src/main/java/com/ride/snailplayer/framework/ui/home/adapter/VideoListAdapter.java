package com.ride.snailplayer.framework.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.ride.snailplayer.databinding.ItemMovieBinding;
import com.ride.snailplayer.framework.base.adapter.databinding.DataBoundAdapter;
import com.ride.snailplayer.framework.base.adapter.databinding.DataBoundViewHolder;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.snailplayer.framework.event.listener.ItemClickListener;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class VideoListAdapter extends DataBoundAdapter<ItemMovieBinding> {
    private List<VideoInfo> videoInfoList;
    private Fragment fragment;
    private ItemClickListener listener;


    /**
     * Creates a DataBoundAdapter with the given item layout
     *
     * @param layoutId The layout to be used for items. It must use data binding.
     */
    public VideoListAdapter(@LayoutRes int layoutId, List<VideoInfo> videoInfoList, Fragment fragment) {
        super(layoutId);
        this.videoInfoList = videoInfoList;
        this.fragment = fragment;
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void bindItem(DataBoundViewHolder<ItemMovieBinding> holder, int position, List<Object> payloads) {
        holder.binding.setVideo(videoInfoList.get(position));
        String url = getUrl(videoInfoList.get(position).img);
        if (listener != null)
            holder.binding.getRoot().setOnClickListener((v -> listener.onClick(videoInfoList.get(position))));
        Glide.with(fragment)
                .load(url)
                .into(holder.binding.movieImage);
    }

    private String getUrl(String oldUrl) {
        int index = oldUrl.lastIndexOf(".");
        String url = oldUrl.substring(0, index) +"_260_360";
        return url + ".jpg";
    }

    @Override
    public int getItemCount() {
        return videoInfoList.size();
    }

}
