package com.ride.snailplayer.framework.ui.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.ride.snailplayer.R;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/18
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class TagAdapter extends DelegateAdapter.Adapter<TagAdapter.TagViewHolder> {
    private String tag;

    public TagAdapter(String tag) {
        this.tag = tag;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper(1);
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new TagViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        holder.tag.setText(tag);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tag;

        public TagViewHolder(View itemView) {
            super(itemView);
            tag = (TextView) itemView.findViewById(R.id.item_tv_tag);
        }
    }
}
