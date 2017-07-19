package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fct.thridparty.vod.entity.PlayInfoList;
import com.fct.thridparty.vod.entity.VideoBase;

/**
 * Created by nick on 2017/7/19.
 */
public class VodPlayUrlInfoResponse extends VodResponse {

    @JsonProperty
    private VideoBase VideoBase;

    @JsonProperty
    private PlayInfoList PlayInfoList;

    @JsonIgnore
    public VideoBase getVideoBase() {
        return VideoBase;
    }

    @JsonIgnore
    public void setVideoBase(VideoBase videoBase) {
        VideoBase = videoBase;
    }

    @JsonIgnore
    public PlayInfoList getPlayInfoList() {
        return PlayInfoList;
    }

    @JsonIgnore
    public void setPlayInfoList(PlayInfoList playInfoList) {
        PlayInfoList = playInfoList;
    }

}
