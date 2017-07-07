package com.ride.snailplayer.framework.base.model;

import cn.bmob.v3.BmobUser;

/**
 * @author Stormouble
 * @since 2017/6/13.
 */

public class User extends BmobUser {
    private String avatarUrl;
    private String nickName;
    private String birthday;
    private String sign;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
