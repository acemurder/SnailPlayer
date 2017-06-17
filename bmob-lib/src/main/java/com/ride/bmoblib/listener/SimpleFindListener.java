package com.ride.bmoblib.listener;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public abstract class SimpleFindListener <T> extends FindListener<T>{

    @Override
    public void done(List<T> list, BmobException e) {
        if (e != null) {
            onError(e);
        } else {
            onResult(list);
        }
    }

    public abstract void onError(BmobException e);

    public abstract void onResult(List<T> list);
}
