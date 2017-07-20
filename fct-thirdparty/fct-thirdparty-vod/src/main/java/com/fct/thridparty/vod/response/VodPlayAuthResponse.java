package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fct.thridparty.vod.entity.VideoMeta;
import lombok.Data;

/**
 * Created by ningyang on 2017/5/22.
 */
public class VodPlayAuthResponse extends VodResponse {

    @JsonProperty
    private String PlayAuth;
    @JsonProperty
    private VideoMeta VideoMeta;

    @JsonIgnore
    public String getPlayAuth() {
        return PlayAuth;
    }

    @JsonIgnore
    public void setPlayAuth(String playAuth) {
        PlayAuth = playAuth;
    }
}
