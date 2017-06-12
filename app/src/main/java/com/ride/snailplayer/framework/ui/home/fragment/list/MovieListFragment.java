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
import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.snailplayer.framework.ui.home.adapter.VideoListAdapter;
import com.ride.snailplayer.framework.ui.play.PlayActivity;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.snailplayer.widget.recyclerview.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import me.dkzwm.smoothrefreshlayout.SmoothRefreshLayout;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class MovieListFragment extends BaseFragment implements VideoListContract.View {
    FragmentMovieListBinding mBinding;
    private String id;
    private String name;
    private VideoListAdapter mAdapter;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private boolean refresh = false;
    private VideoListContract.Presenter presenter;

    public static Fragment newInstance(Bundle bundle) {
        MovieListFragment f = new MovieListFragment();
        f.setArguments(bundle);
        return f;
    }

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
        mBinding.rvVideoList.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        mBinding.rvVideoList.setLayoutManager(gridLayoutManager);
        mBinding.rvVideoList.addOnScrollListener(new EndlessRecyclerViewScrollListener(5) {
            @Override
            public int getFirstVisibleItemPos() {
                return gridLayoutManager.findFirstVisibleItemPosition();
            }

            @Override
            public int getItemCount() {
                return gridLayoutManager.getItemCount();
            }

            @Override
            public void onLoadMore(int page, int totalItemCount) {
                presenter.loadVideoInfo(id,name,page,30);
            }
        });

        mBinding.smoothRefreshLayout.setOnRefreshListener(new SmoothRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                refresh = true;
                presenter.loadVideoInfo(id,name,1,30);
            }

            @Override
            public void onRefreshComplete() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unBind();
    }

    @Override
    public void showProgress() {
        mBinding.smoothRefreshLayout.autoRefresh();
    }

    @Override
    public void stopProgress() {
        mBinding.smoothRefreshLayout.refreshComplete();
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
