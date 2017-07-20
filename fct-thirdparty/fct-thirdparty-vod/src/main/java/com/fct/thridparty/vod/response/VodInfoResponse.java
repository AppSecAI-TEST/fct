package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fct.thridparty.vod.entity.Video;
import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
public class VodInfoResponse extends VodResponse{

    @JsonProperty("Video")
    private Video Video;

    @JsonIgnore
    public com.fct.thridparty.vod.entity.Video getVideo() {
        return Video;
    }

    @JsonIgnore
    public void setVideo(com.fct.thridparty.vod.entity.Video video) {
        Video = video;
    }
}
