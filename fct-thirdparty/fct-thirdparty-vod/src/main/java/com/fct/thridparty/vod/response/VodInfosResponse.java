package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fct.thridparty.vod.entity.Video;

import java.util.List;

/**
 * Created by ningyang on 2017/5/22.
 */
public class VodInfosResponse extends VodResponse {

    @JsonProperty
    private List<Video> VideoList;

    @JsonIgnore
    public List<Video> getVideoList() {
        return VideoList;
    }

    @JsonIgnore
    public void setVideoList(List<Video> videoList) {
        VideoList = videoList;
    }
}
