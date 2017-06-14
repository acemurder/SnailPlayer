package com.ride.util.common;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Stormouble
 * @since 2017/5/27.
 */

public class AppExecutors {

    private static final int DISK_THREAD_CORE_NUM = 4;

    private volatile static AppExecutors sInstance;

    private final Executor mDiskIOExecutor;
    private final Executor mMainThreadExecutor;

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                if (sInstance == null) {
                    sInstance = new AppExecutors();
                }
            }
        }
        return sInstance;
    }

    public AppExecutors() {
        mDiskIOExecutor = new ThreadPoolExecutor(DISK_THREAD_CORE_NUM, DISK_THREAD_CORE_NUM,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mMainThreadExecutor = new MainThreadExecutor();
    }

    public Executor getDiskIOExecutor() {
        return mDiskIOExecutor;
    }

    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
