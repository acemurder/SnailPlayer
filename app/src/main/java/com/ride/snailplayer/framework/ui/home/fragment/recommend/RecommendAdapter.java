package com.ride.snailplayer.framework.ui.home.fragment.recommend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.ride.snailplayer.net.model.RecommendItem;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/15
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class RecommendAdapter extends VirtualLayoutAdapter {

    private List<RecommendItem> items;
    private List<LayoutHelper> helpers;

    private static final int TYPE_LOOPER_VIEW = 1;
    private static final int TYPE_TYPE_TAG = 2;
    private static final int TYPE_VIDEO_ITEM = 3;


    public RecommendAdapter(@NonNull VirtualLayoutManager layoutManager, List<RecommendItem> items, List<LayoutHelper> helpers) {
        super(layoutManager);
        this.items = items;
        this.helpers = helpers;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOOPER_VIEW)

            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_LOOPER_VIEW;
        else if (isTag(position)) {

            return TYPE_TYPE_TAG;
        } else
            return TYPE_VIDEO_ITEM;

    }

    private boolean isTag(int postion) {
        for (LayoutHelper helper : helpers) {
            if (postion == helper.getItemCount())
                return true;
        }
        return false;
    }

    private int[] getTruePosition(int position){
        int[] matrix = new int[2];
        int count = 0;
        int totalPosition = 0;
        for (LayoutHelper helper : helpers){
            if (position >= totalPosition && position < totalPosition + helper.getItemCount()){
                matrix[0] = count - 1;
                matrix[1] = position - totalPosition - count - 1;
                return matrix;
            }
            count += 1;
            totalPosition += helper.getItemCount();
        }
        return matrix;


    }


    @Override
    public int getItemCount() {
        int count = 0;
        for (LayoutHelper helper : helpers)
            count += helper.getItemCount();
        return count;
    }
}
