package com.ride.snailplayer.net.func;

import com.ride.snailplayer.net.model.Resource;
import com.ride.snailplayer.net.exception.SnailPlayerApiException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author Stormouble
 * @since 2017/5/24.
 */

public class FuncMapResourceToData <T> implements Function<Resource<T>, T> {

    private static final FuncMapResourceToData<Object> INSTANCE =
            new FuncMapResourceToData<>();

    @SuppressWarnings("unchecked")
    public static <T> FuncMapResourceToData<T> instance() {
        return (FuncMapResourceToData<T>) INSTANCE;
    }

    @Override
    public T apply(@NonNull Resource<T> resource) throws Exception {
        if (resource.code == 100000 || resource.code == 0) {
            return resource.data;
        } else {
            throw new SnailPlayerApiException("code=" + resource.code + ",errorMssage=" + resource.errorMessage);
        }
    }
}
