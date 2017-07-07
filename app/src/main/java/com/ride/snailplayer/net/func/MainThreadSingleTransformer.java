package com.ride.snailplayer.net.func;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stormouble
 * @since 2017/7/7.
 */

public class MainThreadSingleTransformer<T> implements SingleTransformer<T, T>{

    private static final MainThreadSingleTransformer<Object> INSTANCE =
            new MainThreadSingleTransformer<>();

    @SuppressWarnings("unchecked")
    public static <T> MainThreadSingleTransformer<T> instance() {
        return (MainThreadSingleTransformer<T>) INSTANCE;
    }

    @Override
    public SingleSource<T> apply(@NonNull Single<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
