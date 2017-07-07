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
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author Stormouble
 * @since 2017/6/7.
 */

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Channel>> mPreloadChannels
            = new MutableLiveData<>();

    public HomeViewModel(Application application) {
        super(application);
        initPreloadChannels(getChannelsData());
    }

    private void initPreloadChannels(Observable<List<Channel>> obserChannels) {
        obserChannels.subscribe(mPreloadChannels::setValue);
    }

    private Observable<List<Channel>> getChannelsData() {
        return getFrontChannels().flatMap(new Function<List<Channel>, ObservableSource<List<Channel>>>() {
                    @Override
                    public ObservableSource<List<Channel>> apply(@NonNull List<Channel> frontChannels) throws Exception {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    getApplication().getApplicationContext().getAssets().open("channel.json")));
                            List<Channel> localChannels = new Gson()
                                    .fromJson(reader, new TypeToken<List<Channel>>() {}.getType());
                            frontChannels.addAll(localChannels);
                            return Observable.just(frontChannels);
                        } finally {
                            IOUtils.closeQuietly(reader);
                        }
                    }
                });
    }

    private Observable<List<Channel>> getFrontChannels() {
        return Observable.create(e -> {
            List<Channel> channels = new ArrayList<>();
            Channel recommendChannel = new Channel();
            recommendChannel.id = "0";
            recommendChannel.name = "推荐";
            recommendChannel.describe = "推荐";
            channels.add(recommendChannel);

            if (!e.isDisposed()) {
                e.onNext(channels);
                e.onComplete();
            }
        });
    }

    public LiveData<List<Channel>> getPreloadChannelList() {
        return mPreloadChannels;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPreloadChannels.setValue(null);
    }
}
