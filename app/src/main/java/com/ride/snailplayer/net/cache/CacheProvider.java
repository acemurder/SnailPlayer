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
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<List<RecommendItem>>>qiyiRecommendDetail(Observable<List<RecommendItem>>  recommendItem,
                                                                 DynamicKey key,
                                                                 EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = 2, timeUnit = TimeUnit.DAYS)
    Observable<Reply<ChannelDetail>> qiyiChannelDetail(Observable<ChannelDetail>
                                                                              channelDetail,
                                                        DynamicKey key,
                                                        EvictDynamicKey evictDynamicKey);


}
