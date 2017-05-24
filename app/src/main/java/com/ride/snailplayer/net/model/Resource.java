package com.ride.snailplayer.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A generic class that holds a value.
 *
 * @param <T>
 */
public class Resource<T> implements Serializable {

    /**
     * 服务器返回码，100000=成功
     */
    @SerializedName("code")
    public int code;

    @SerializedName("data")
    public T data;

    /**
     * 失败信息，返回失败时不返回此字段
     */
    @SerializedName("errmsg")
    public String errorMessage;

}
