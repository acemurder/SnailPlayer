package com.ride.snailplayer.framework.ui.home.fragment.list;

import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.ChannelDetail;
import com.ride.snailplayer.net.model.Resource;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;
import com.ride.util.common.log.Timber;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

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
//        observer = new Observer<ChannelDetail>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                mDisposables.add(d);
//                view.showProgress();
//            }
//
//            @Override
//            public void onNext(@NonNull <ChannelDetail> channelDetailResource) {
//                if (channelDetailResource.code == SnailPlayerConfig.API_CODE_SUCCESS){
//                   if (channelDetailResource == null || channelDetailResource.data == null || channelDetailResource.data.videoInfoList == null){
//                       view.onEmptyData();
//                   }else
//                    view.onSuccess(channelDetailResource.data.videoInfoList);
//
//                }
//                else if (channelDetailResource.code == SnailPlayerConfig.API_CODE_NO_DATA)
//                    view.onEmptyData();
//                else
//                    view.onServerError();
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                view.stopProgress();
//                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException)
//                    view.onNetworkError();
//                else
//                    view.onServerError();
//            }
//
//            @Override
//            public void onComplete() {
//                view.stopProgress();
//            }
//        };
    }

    @Override
    public void loadVideoInfo(String id, String name, int page, int size) {
        Timber.i(id, name, page, size);
        Observable<ChannelDetail> o = ApiClient.IQIYI.
                getIQiYiApiService().qiyiChannelDetail(IQiYiApiParamsUtils.genChannelDetailParams(id, name, page, size))
                .map(channelDetailResource -> channelDetailResource.data);
        ApiClient.IQIYI.getCacheProviders().qiyiChannelDetail(o,
                new DynamicKey(name + page), new EvictDynamicKey(false)).
                map(Reply::getData).compose(MainThreadObservableTransformer.instance()).subscribe(new Observer<ChannelDetail>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                view.showProgress();
            }

            @Override
            public void onNext(@NonNull ChannelDetail channelDetail) {
                if (channelDetail != null && channelDetail.videoInfoList != null) {
                    view.onSuccess(channelDetail.videoInfoList);
                }else
                    view.onEmptyData();
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
        });

//        ApiClient.IQIYI.getIQiYiApiService().qiyiChannelDetail(IQiYiApiParamsUtils.genChannelDetailParams(id,name,page,size))
//                .compose(MainThreadObservableTransformer.instance()).subscribe(observer);
    }

    @Override
    public void unBind() {
        mDisposables.dispose();
        this.view = null;
    }
}
