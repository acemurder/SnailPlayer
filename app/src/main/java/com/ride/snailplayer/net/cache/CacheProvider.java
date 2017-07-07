package com.ride.snailplayer.net.cache;

import com.ride.snailplayer.net.model.ChannelDetail;
import com.ride.snailplayer.net.model.RecommendItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Reply;

/**
 * Created by ：AceMurder
 * Created on ：2017/7/7
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public interface CacheProvider {
    @LifeCache(duration = 60, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<RecommendItem>>>qiyiRecommendDetail(Observable<List<RecommendItem>>  recommendItem,
                                                                 DynamicKey key,
                                                                 EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = 5, timeUnit = TimeUnit.HOURS)
    Observable<Reply<ChannelDetail>> qiyiChannelDetail(Observable<ChannelDetail>
                                                                              channelDetail,
                                                        DynamicKey key,
                                                        EvictDynamicKey evictDynamicKey);


}
