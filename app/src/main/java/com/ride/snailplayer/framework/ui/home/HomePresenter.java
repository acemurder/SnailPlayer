package com.ride.snailplayer.framework.ui.home;

import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.snailplayer.net.model.Resource;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.List;

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

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View view;
    private Observer<Resource<List<Channel>>> observer;
    protected CompositeDisposable mDisposables = new CompositeDisposable();


    public HomePresenter(HomeContract.View view) {
        this.view = view;
        observer = new Observer<Resource<List<Channel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposables.add(d);
            }

            @Override
            public void onNext(@NonNull Resource<List<Channel>> listResource) {
                if (listResource.code == SnailPlayerConfig.API_CODE_SUCCESS)
                    view.onSuccess(listResource.data);
               else
                    view.onServerError();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException)
                    view.onNetworkError();
                else
                    view.onServerError();
            }

            @Override
            public void onComplete() {
            }
        };
    }

    @Override
    public void loadChannels() {
        ApiClient.IQIYI.getIQiYiApiService().qiyiChannelList(IQiYiApiParamsUtils.genChannelParams())
                .compose(MainThreadObservableTransformer.instance()).subscribe(observer);
    }

    @Override
    public void unBind() {
        mDisposables.dispose();
        this.view = null;
    }
}
