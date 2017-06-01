package com.ride.snailplayer.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/1
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public abstract class LoadMoreRecyclerViewListener extends RecyclerView.OnScrollListener {

    private GridLayoutManager gridLayoutManager;

    private int currentPage = 1;

    private int previousTotalItemCount = 0;

    private boolean loading = true;

    private int startingPageIndex = 1;

    private int visibleThreshold = 5;

    public LoadMoreRecyclerViewListener(GridLayoutManager gridLayoutManager, int visibleThreshold) {
        this.gridLayoutManager = gridLayoutManager;
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = gridLayoutManager.getItemCount();

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemCount);

}
