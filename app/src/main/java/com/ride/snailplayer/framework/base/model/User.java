package com.ride.snailplayer.framework.base.model;

import cn.bmob.v3.BmobUser;

/**
 * @author Stormouble
 * @since 2017/6/13.
 */

public class User extends BmobUser {
    private String nickName;
    private String avatarUrl;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
