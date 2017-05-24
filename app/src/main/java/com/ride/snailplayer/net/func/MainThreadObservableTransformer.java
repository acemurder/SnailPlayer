package com.ride.snailplayer.net.func;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Stormouble
 * @since 2016/12/24.
 */

public class MainThreadObservableTransformer<T> implements ObservableTransformer<T, T> {

    private static final MainThreadObservableTransformer<Object> INSTANCE =
            new MainThreadObservableTransformer<>();

    @SuppressWarnings("unchecked")
    public static <T> MainThreadObservableTransformer<T> instance() {
        return (MainThreadObservableTransformer<T>) INSTANCE;
    }

    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
