package com.ride.snailplayer.framework.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ride.snailplayer.framework.base.model.User;
import com.ride.util.common.util.RegexUtils;

import cn.bmob.v3.BmobUser;
import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public class UserViewModel extends ViewModel {

    private User mUser;

    public UserViewModel() {
        mUser = BmobUser.getCurrentUser(User.class);
    }

    public User getUser() {
        return mUser;
    }

    public Observable<Bitmap> getUserAvatar() {
        return null;
    }

    public Completable updateAvatar(String url) {

//        if (TextUtils.isEmpty(url) || !RegexUtils.isURL(url)) {
//            return;
//        }
        return null;

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
