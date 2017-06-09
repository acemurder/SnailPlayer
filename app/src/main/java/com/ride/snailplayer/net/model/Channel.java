package com.ride.snailplayer.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 频道信息
 */
public class Channel implements Serializable{
    /**
     * 频道ID
     */
    @SerializedName("id")
    public String id;

    /**
     * 频道名称
     */
    @SerializedName("name")
    public String name;

    /**
     * 描述
     */
    @SerializedName("desc")
    public String describe;
}
