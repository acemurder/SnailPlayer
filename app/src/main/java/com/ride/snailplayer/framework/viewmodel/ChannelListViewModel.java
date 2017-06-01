package com.ride.snailplayer.framework.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.ride.snailplayer.framework.repository.ChannelListRepository;
import com.ride.snailplayer.net.model.Channel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ：AceMurder
 * Created on ：2017/5/31
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class ChannelListViewModel extends ViewModel {
    private LiveData<Observable<List<Channel>>> channelList;
    private ChannelListRepository repository;


    public ChannelListViewModel() {
        repository = new ChannelListRepository();
    }

    public void init() {
        if (this.channelList != null) {
            // ViewModel is created per Fragment so
            // we know the userId won't change
            return;
        }
        channelList = repository.getChannels();
    }

    public LiveData<Observable<List<Channel>>> getChannelList() {
        return this.channelList;
    }
}
