package com.ride.snailplayer.framework.base.model;

import cn.bmob.v3.BmobUser;

/**
 * @author Stormouble
 * @since 2017/6/13.
 */

public class User extends BmobUser {

    private String avatraUrl;

    public String getAvatraUrl() {
        return avatraUrl;
    }

    public void setAvatraUrl(String avatraUrl) {
        this.avatraUrl = avatraUrl;
    }
}
