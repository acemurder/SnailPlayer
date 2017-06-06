package com.ride.snailplayer.framework.ui.home.fragment.list;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentMovieListBinding;
import com.ride.snailplayer.framework.ui.home.adapter.VideoListAdapter;
import com.ride.snailplayer.framework.ui.play.PlayActivity;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.snailplayer.widget.LoadMoreRecyclerViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class MovieListFragment extends Fragment implements VideoListContract.View {
    FragmentMovieListBinding mBinding;
    private String id;
    private String name;
    private VideoListAdapter mAdapter;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private boolean refresh = false;
    private VideoListContract.Presenter presenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list,container,false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new VideoListPresenter(this);
        init();
        presenter.loadVideoInfo(id,name,1,30);


    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null){
            id = bundle.getString("id");
            name = bundle.getString("name");
        }
        mAdapter = new VideoListAdapter(R.layout.item_movie, videoInfoList, this);
        mAdapter.setListener((videoInfo -> PlayActivity.launchActivity(getActivity(),videoInfo)));
        mBinding.videoList.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        mBinding.videoList.setLayoutManager(gridLayoutManager);
        mBinding.videoList.addOnScrollListener(new LoadMoreRecyclerViewListener(gridLayoutManager,5) {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                presenter.loadVideoInfo(id,name,page,30);
            }
        });

       // loadData(1,50);
        mBinding.refreshLayout.setOnRefreshListener(()->{
           // loadData(1,50);
            refresh = true;
            presenter.loadVideoInfo(id,name,1,30);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unBind();
    }

    @Override
    public void showProgress() {
        mBinding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopProgress() {
        mBinding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onSuccess(List<VideoInfo> videoInfoList) {
        if (refresh) {
            this.videoInfoList.clear();
            refresh = false;
        }
        int count = this.videoInfoList.size();
        this.videoInfoList.addAll(videoInfoList);
        mAdapter.notifyItemRangeChanged(count,videoInfoList.size());
    }

    @Override
    public void onEmptyData() {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onServerError() {

    }
}
