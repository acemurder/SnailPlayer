package com.ride.snailplayer.framework.ui.fragment;

import com.ride.snailplayer.net.model.VideoInfo;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/1
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public interface VideoListContract {
    interface View{
        void showProgress();
        void stopProgress();
        void onSuccess(List<VideoInfo> videoInfoList);
        void onEmptyData();
        void onNetworkError();
        void onServerError();
    }

    interface Presenter{
        void loadVideoInfo(String id,String name,int page,int size);
        void unBind();
    }

}
