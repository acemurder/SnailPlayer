package com.ride.snailplayer.framework.ui.activity;

import com.ride.snailplayer.net.model.Channel;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/1
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public interface HomeContract{
    interface View{
        void onSuccess(List<Channel> channelList);
        void onNetworkError();
        void onServerError();
    }

    interface Presenter{
        void loadChannels();
        void unBind();
    }
}
