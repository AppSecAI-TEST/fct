package com.fct.thridparty.vod.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nick on 2017/7/19.
 */
public class PlayInfo {

    @JsonProperty
    private String Bitrate;
    @JsonProperty
    private String Definition;
    @JsonProperty
    private String Duration;
    @JsonProperty
    private String Encrypt;
    @JsonProperty
    private String PlayURL;
    @JsonProperty
    private String Format;
    @JsonProperty
    private String Fps;
    @JsonProperty
    private Long Size;
    @JsonProperty
    private Long Width;
    @JsonProperty
    private Long Height;

    @JsonIgnore
    public String getBitrate() {
        return Bitrate;
    }

    @JsonIgnore
    public void setBitrate(String bitrate) {
        Bitrate = bitrate;
    }

    @JsonIgnore
    public String getDefinition() {
        return Definition;
    }

    @JsonIgnore
    public void setDefinition(String definition) {
        Definition = definition;
    }

    @JsonIgnore
    public String getDuration() {
        return Duration;
    }

    @JsonIgnore
    public void setDuration(String duration) {
        Duration = duration;
    }

    @JsonIgnore
    public String getEncrypt() {
        return Encrypt;
    }

    @JsonIgnore
    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }

    @JsonIgnore
    public String getPlayURL() {
        return PlayURL;
    }

    @JsonIgnore
    public void setPlayURL(String playURL) {
        PlayURL = playURL;
    }

    @JsonIgnore
    public String getFormat() {
        return Format;
    }

    @JsonIgnore
    public void setFormat(String format) {
        Format = format;
    }

    @JsonIgnore
    public String getFps() {
        return Fps;
    }

    @JsonIgnore
    public void setFps(String fps) {
        Fps = fps;
    }

    @JsonIgnore
    public Long getSize() {
        return Size;
    }

    @JsonIgnore
    public void setSize(Long size) {
        Size = size;
    }

    @JsonIgnore
    public Long getWidth() {
        return Width;
    }

    @JsonIgnore
    public void setWidth(Long width) {
        Width = width;
    }

    @JsonIgnore
    public Long getHeight() {
        return Height;
    }

    @JsonIgnore
    public void setHeight(Long height) {
        Height = height;
    }
}
