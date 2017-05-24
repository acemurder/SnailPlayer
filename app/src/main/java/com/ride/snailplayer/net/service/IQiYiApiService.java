package com.ride.snailplayer.net.service;

import com.ride.snailplayer.net.model.ChannelDetail;
import com.ride.snailplayer.net.model.Channel;
import com.ride.snailplayer.net.model.RecommendItem;
import com.ride.snailplayer.net.model.Resource;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * @author Stormouble
 * @since 2017/5/19.
 */
public interface IQiYiApiService {

    /**
     * 获取频道列表接口
     * @param params
     * @return
     */
    @GET("channel")
    Observable<Resource<List<Channel>>> qiyiChannelList(@QueryMap Map<String, String> params);

    /**
     * 获取频道详情接口
     * @param params
     * @return
     */
    @GET("channel")
    Observable<Resource<ChannelDetail>> qiyiChannelDetail(@QueryMap Map<String, String> params);

    /**
     * 获取推荐页数据接口
     * @param params
     * @return
     */
    @GET("recommend")
    Observable<Resource<List<RecommendItem>>> qiyiRecommendDetail(@QueryMap Map<String, String> params);
}
