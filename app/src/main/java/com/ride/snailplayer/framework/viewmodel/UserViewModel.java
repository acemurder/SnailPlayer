package com.ride.snailplayer.framework.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ride.snailplayer.framework.base.model.User;

import cn.bmob.v3.BmobUser;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public class UserViewModel {

    public static User getCurrentUser() {
        return BmobUser.getCurrentUser(User.class);
    }


}
