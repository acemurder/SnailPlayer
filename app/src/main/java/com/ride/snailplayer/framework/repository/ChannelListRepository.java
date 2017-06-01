package com.ride.snailplayer.framework.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.FuncMapResourceToData;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.snailplayer.net.util.IQiYiApiParamsUtils;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Stormouble
 * @since 2017/5/24.
 */

public class ChannelListRepository {


    public LiveData<Observable<List<Channel>>> getChannels() {
        final MutableLiveData<Observable<List<Channel>>> data = new MutableLiveData<>();
        Observable<List<Channel>> observable =  ApiClient.IQIYI
                .getIQiYiApiService()
                .qiyiChannelList(IQiYiApiParamsUtils.genChannelParams())
                .map(FuncMapResourceToData.<List<Channel>>instance())
                .compose(MainThreadObservableTransformer.<List<Channel>>instance());
        data.setValue(observable);
        return data;
    }
}
