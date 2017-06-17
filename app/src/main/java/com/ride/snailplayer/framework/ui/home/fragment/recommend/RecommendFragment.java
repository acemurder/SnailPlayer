package com.ride.snailplayer.framework.ui.home.fragment.recommend;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentMovieListBinding;
import com.ride.snailplayer.framework.ui.home.adapter.LooperAdapter;
import com.ride.snailplayer.framework.ui.home.adapter.RecommendListAdapter;
import com.ride.snailplayer.framework.ui.home.adapter.TagAdapter;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.FuncMapResourceToData;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.RecommendItem;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;
import com.ride.util.common.log.Timber;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.dkzwm.smoothrefreshlayout.SmoothRefreshLayout;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/2
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class RecommendFragment extends Fragment {
    FragmentMovieListBinding mBinding;

    private VirtualLayoutManager manager;
    private DelegateAdapter delegateAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list,container,false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        manager = new VirtualLayoutManager(getActivity());
        mBinding.rvVideoList.setLayoutManager(manager);
        delegateAdapter = new DelegateAdapter(manager, false);
        mBinding.rvVideoList.setAdapter(delegateAdapter);
        mBinding.smoothRefreshLayout.setOnRefreshListener(new SmoothRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshBegin(final boolean isRefresh) {
                loadData();
            }

            @Override
            public void onRefreshComplete() {

            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData(){
        ApiClient.IQIYI.getIQiYiApiService().qiyiRecommendDetail(IQiYiApiParamsUtils.genRecommendDetailParams(1,30))
                .compose(MainThreadObservableTransformer.instance())
                .map(FuncMapResourceToData.instance())
                .subscribe(new Observer<List<RecommendItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mBinding.smoothRefreshLayout.autoRefresh();

                    }

                    @Override
                    public void onNext(@NonNull List<RecommendItem> recommendItems) {
                        setupLayoutHelpers(recommendItems);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mBinding.smoothRefreshLayout.refreshComplete();
                    }

                    @Override
                    public void onComplete() {
                        mBinding.smoothRefreshLayout.refreshComplete();

                    }
                });
    }

    private void setupLayoutHelpers(List<RecommendItem> items) {
        for (RecommendItem r : items) {
            if (r.title.equals("轮播图")) {
                delegateAdapter.addAdapter(new LooperAdapter(r.videoInfoList,this));
                break;
            }
        }
        for (RecommendItem item: items){
            Timber.i( item.title + ":"+ item.videoInfoList.size() + " ");
            if (item.title.equals("轮播图")){

            }else {
                delegateAdapter.addAdapter(new TagAdapter(item.title));
                delegateAdapter.addAdapter(new RecommendListAdapter(item.videoInfoList,this));
            }
        }
    }
}
