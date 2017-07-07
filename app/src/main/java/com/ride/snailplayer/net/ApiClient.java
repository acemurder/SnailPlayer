package com.ride.snailplayer.net;

import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.snailplayer.net.cache.CacheProvider;
import com.ride.snailplayer.net.service.IQiYiApiService;
import com.ride.util.common.util.AppUtils;
import com.ride.util.common.util.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Stormouble
 * @since 2017/5/19.
 */

public enum ApiClient {

    IQIYI(SnailPlayerConfig.API_REALTIME_HOST) {
        @Override
        public Retrofit initRetrofit(String host) {
            return new Retrofit.Builder()
                    .baseUrl(host)
                    .client(mOkHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        @Override
        public OkHttpClient initOkHttpClient() {
            File httpCacheDirectory = new File(Utils.getContext().getCacheDir(), "responses");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (AppUtils.isDebug()) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
            }
          //  builder.cache(cache);
         //   builder.addInterceptor(new CacheInterceptor());
            builder.connectTimeout(SnailPlayerConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(SnailPlayerConfig.READ_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(SnailPlayerConfig.WRITE_TIMEOUT, TimeUnit.SECONDS);
            return builder.build();
        }
    };

    final Retrofit mRetrofit;
    final OkHttpClient mOkHttpClient;

    final IQiYiApiService mIQiYiApiService;
    final CacheProvider cacheProviders;

    ApiClient(String host) {
        mOkHttpClient = initOkHttpClient();
        mRetrofit = initRetrofit(host);
        File httpCacheDirectory = new File(Utils.getContext().getCacheDir(), "responses");
        if (!httpCacheDirectory.exists())
            httpCacheDirectory.mkdirs();

        cacheProviders = new RxCache.Builder()
                .persistence(httpCacheDirectory, new GsonSpeaker())
                .using(CacheProvider.class);

        mIQiYiApiService = mRetrofit.create(IQiYiApiService.class);


    }

    public abstract Retrofit initRetrofit(String host);

    public abstract OkHttpClient initOkHttpClient();

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public IQiYiApiService getIQiYiApiService() {
        return mIQiYiApiService;
    }

    public CacheProvider getCacheProviders() {
        return cacheProviders;
    }
}
