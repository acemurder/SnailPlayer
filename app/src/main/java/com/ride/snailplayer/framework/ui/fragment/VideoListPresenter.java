package com.ride.snailplayer.framework.ui.fragment;

import com.ride.snailplayer.common.config.SnailPlayerConfig;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.ChannelDetail;
import com.ride.snailplayer.net.model.Resource;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/1
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class VideoListPresenter implements VideoListContract.Presenter {
    private VideoListContract.View view;
    private Observer<Resource<ChannelDetail>> observer;
    protected CompositeDisposable mDisposables = new CompositeDisposable();


    public VideoListPresenter(VideoListContract.View view) {
        this.view = view;
        observer = new Observer<Resource<ChannelDetail>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposables.add(d);
                view.showProgress();
            }

            @Override
            public void onNext(@NonNull Resource<ChannelDetail> channelDetailResource) {
                if (channelDetailResource.code == SnailPlayerConfig.API_CODE_SUCCESS)
                    view.onSuccess(channelDetailResource.data.videoInfoList);
                else if (channelDetailResource.code == SnailPlayerConfig.API_CODE_NO_DATA)
                    view.onEmptyData();
                else
                    view.onServerError();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                view.stopProgress();
                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException)
                    view.onNetworkError();
                else
                    view.onServerError();
            }

            @Override
            public void onComplete() {
                view.stopProgress();
            }
        };
    }

    @Override
    public void loadVideoInfo(String id, String name, int page, int size) {
        ApiClient.IQIYI.getIQiYiApiService().qiyiChannelDetail(IQiYiApiParamsUtils.genChannelDetailParams(id,name,page,size))
                .compose(MainThreadObservableTransformer.instance()).subscribe(observer);
    }

    @Override
    public void unBind() {
        mDisposables.dispose();
        this.view = null;
    }
}
