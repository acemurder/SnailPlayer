package com.ride.snailplayer.net;


import android.arch.lifecycle.Transformations;

import com.ride.snailplayer.common.config.SnailPlayerConfig;
import com.ride.snailplayer.net.service.IQiYiApiService;
import com.ride.util.common.AppUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

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
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (AppUtils.isDebug()) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
            }
            builder.connectTimeout(SnailPlayerConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(SnailPlayerConfig.READ_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(SnailPlayerConfig.WRITE_TIMEOUT, TimeUnit.SECONDS);
            return builder.build();
        }
    };

    final Retrofit mRetrofit;
    final OkHttpClient mOkHttpClient;

    final IQiYiApiService mIQiYiApiService;

    ApiClient(String host) {
        mOkHttpClient = initOkHttpClient();
        mRetrofit = initRetrofit(host);

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
}
