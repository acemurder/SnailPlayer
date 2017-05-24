package com.ride.snailplayer.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐页每个频道数据
 */
public class RecommendItem implements Serializable {
    @SerializedName("title")
    public String title;

    @SerializedName("channel_id")
    public String channelId;

    @SerializedName("channel_name")
    public String channelName;

    @SerializedName("video_list")
    public List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
}
