package com.ride.snailplayer.framework.ui.home.fragment.recommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ride.snailplayer.databinding.FragmentRecommendBinding;
import com.ride.snailplayer.framework.ui.home.adapter.LoopAdapter;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.FuncMapResourceToData;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.RecommendItem;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;
import com.ride.util.common.log.Timber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/2
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class RecommendFragment extends Fragment {
    private FragmentRecommendBinding mBinding;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private LoopAdapter mLoopAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRecommendBinding.inflate(inflater,container,false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mLoopAdapter = new LoopAdapter(mBinding.loopPagerView,videoInfoList);
        mBinding.loopPagerView.getViewPager().setPageMargin(20);
        mBinding.loopPagerView.getViewPager().setOffscreenPageLimit(4);

      //  mBinding.loopPagerView.getViewPager().setPageTransformer(true,new ScrollOffsetTransformer());
        RelativeLayout.LayoutParams paramTest = (RelativeLayout.LayoutParams) mBinding.loopPagerView.getViewPager().getLayoutParams();
        paramTest.leftMargin = 80;
        paramTest.rightMargin = 80;
        mBinding.loopPagerView.getViewPager().setLayoutParams(paramTest);
        mBinding.loopPagerView.getViewPager().setClipChildren(false);
        mBinding.loopPagerView.setAdapter(mLoopAdapter);
        mBinding.loopPagerView.setHintView(null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiClient.IQIYI.getIQiYiApiService().qiyiRecommendDetail(IQiYiApiParamsUtils.genRecommendDetailParams(1,30))
                .compose(MainThreadObservableTransformer.instance())
                .map(FuncMapResourceToData.instance())
                .subscribe(new Observer<List<RecommendItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<RecommendItem> recommendItems) {
                        showLoopView(recommendItems);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showLoopView(List<RecommendItem> recommendItems) {
        for (RecommendItem r : recommendItems){
            if (r.title.equals("轮播图")){
                for(VideoInfo v:r.videoInfoList)
                    Timber.e(v.shortTitle);
                videoInfoList.addAll(r.videoInfoList);

                mLoopAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
