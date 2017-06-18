package com.ride.snailplayer.framework.ui.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.bumptech.glide.Glide;
import com.ride.snailplayer.R;
import com.ride.snailplayer.framework.ui.play.PlayActivity;
import com.ride.snailplayer.net.model.VideoInfo;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/18
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class RecommendListAdapter  extends DelegateAdapter.Adapter<RecommendListAdapter.VideoListHolder>{
    private List<VideoInfo> videoInfoList ;
    private Fragment fragment;

    public RecommendListAdapter(List<VideoInfo> videoInfoList, Fragment fragment) {
        this.videoInfoList = videoInfoList;
        this.fragment = fragment;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new GridLayoutHelper(2,videoInfoList.size());
    }

    @Override
    public VideoListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoListHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_video, parent, false));
    }

    @Override
    public void onBindViewHolder(VideoListHolder holder, int position) {
        holder.itemView.setOnClickListener((v -> PlayActivity.launchActivity(fragment.getActivity(),
                videoInfoList.get(position))));
        holder.title.setText(videoInfoList.get(position).title);
        holder.content.setText(videoInfoList.get(position).shortTitle);
        Glide.with(fragment).load(getUrl(videoInfoList.get(position).img)).into(holder.img);
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

    class VideoListHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView title;
        TextView content;
        ImageView img;

        public VideoListHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.item_recommend_tv_title);
            content = (TextView) itemView.findViewById(R.id.item_recommend_tv_content);
            img = (ImageView) itemView.findViewById(R.id.item_recommend_iv_img);
        }
    }
}
