package com.ride.snailplayer.framework.ui.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stormouble
 * @since 2017/6/7.
 */

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Channel>> mPreloadChannelList
            = new MutableLiveData<>();

    public HomeViewModel(Application application) {
        super(application);
        setupChannelListData();
    }

    private void setupChannelListData() {
        Observable<List<Channel>> obserPreChannelData = Observable.create(e -> {
            List<Channel> channels = new ArrayList<>();
            Channel recommendChannel = new Channel();
            recommendChannel.id = "0";
            recommendChannel.name = "推荐";
            recommendChannel.describe = "推荐";
            channels.add(recommendChannel);

            if (!e.isDisposed()) {
                e.onNext(channels);
            } else {
                e.onComplete();
            }
        });

        Observable<List<Channel>> obserLocalChannelData = Observable.create(e -> {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        getApplication().getApplicationContext().getAssets().open("channel.json")));
                List<Channel> localList = new Gson()
                        .fromJson(reader, new TypeToken<List<Channel>>() {
                        }.getType());

                if (!e.isDisposed()) {
                    e.onNext(localList);
                } else {
                    e.onComplete();
                }
            } finally {
                IOUtils.closeQuietly(reader);
            }
        });

        Observable.zip(obserPreChannelData, obserLocalChannelData, (preChannels, localChannels) -> {
            preChannels.addAll(localChannels);
            return preChannels;
        }).compose(MainThreadObservableTransformer.instance()).subscribe(mPreloadChannelList::setValue, ignored -> Timber.e("读取assets channel.json文件错误"));
    }

    public LiveData<List<Channel>> getPreloadChannelList() {
        return mPreloadChannelList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPreloadChannelList.setValue(null);
    }
}
