package com.ride.snailplayer.framework.ui.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.net.model.Channel;
import com.ride.util.common.util.IOUtils;
import com.ride.util.common.log.Timber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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
        Observable.just(new ArrayList<Channel>())
                .doOnNext(channels -> {
                    Channel recommendChannel = new Channel();
                    recommendChannel.id = "0";
                    recommendChannel.name = "推荐";
                    recommendChannel.describe = "推荐";
                    channels.add(recommendChannel);
                })
                .switchMap(new Function<ArrayList<Channel>, ObservableSource<ArrayList<Channel>>>() {
                    @Override
                    public ObservableSource<ArrayList<Channel>> apply(@NonNull ArrayList<Channel> channels) throws Exception {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    getApplication().getApplicationContext().getAssets().open("channel.json")));
                            List<Channel> preloadList = new Gson()
                                    .fromJson(reader, new TypeToken<List<Channel>>() {}.getType());
                            channels.addAll(preloadList);
                        } finally {
                            IOUtils.closeQuietly(reader);
                        }
                        return Observable.just(channels);
                    }
                })
                .compose(MainThreadObservableTransformer.<ArrayList<Channel>>instance())
                .subscribe(mPreloadChannelList::setValue, ignored -> {});
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
