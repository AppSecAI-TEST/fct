package com.fct.thridparty.vod.response;

import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by ningyang on 2017/5/23.
 */
public class VodUploadResponse extends VodResponse {

    @JsonProperty
    private String videoId;

    public VodUploadResponse(UploadVideoResponse response){
        if(response!=null){
            videoId = response.getVideoId();
            setCode(response.getCode());
            setMessage(response.getMessage());
        }
    }

    @JsonIgnore
    public String getVideoId() {
        return videoId;
    }

    @JsonIgnore
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
