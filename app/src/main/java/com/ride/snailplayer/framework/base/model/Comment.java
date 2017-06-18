package com.ride.snailplayer.framework.base.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/18
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class Comment extends BmobObject {
    private String tvId;
    private String userName;
    private String content;
    private String userAvatar;

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTvId() {
        return tvId;
    }

    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
