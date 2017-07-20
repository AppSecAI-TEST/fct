package com.fct.thridparty.vod.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nick on 2017/7/19.
 */
public class PlayInfoList {

    @JsonProperty
    private PlayInfo[] PlayInfo;

    @JsonIgnore
    public PlayInfo[] getPlayInfo() {
        return PlayInfo;
    }

    @JsonIgnore
    public void setPlayInfo(PlayInfo[] playInfo) {
        PlayInfo = playInfo;
    }
}
