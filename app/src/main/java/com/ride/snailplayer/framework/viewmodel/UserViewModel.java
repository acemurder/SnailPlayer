package com.ride.snailplayer.framework.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ride.snailplayer.framework.base.model.User;

import cn.bmob.v3.BmobUser;

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

    public void updateUser() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
