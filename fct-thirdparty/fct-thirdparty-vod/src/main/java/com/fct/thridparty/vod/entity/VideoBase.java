package com.fct.thridparty.vod.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nick on 2017/7/19.
 */
public class VideoBase {

    @JsonProperty
    private String VideoId;
    @JsonProperty
    private String Title;
    @JsonProperty
    private String Duration;
    @JsonProperty
    private String CoverURL;
    @JsonProperty
    private String Status;
    @JsonProperty
    private String CreationTime;
    @JsonProperty
    private String MediaType;

    @JsonIgnore
    public String getVideoId() {
        return VideoId;
    }

    @JsonIgnore
    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    @JsonIgnore
    public String getTitle() {
        return Title;
    }

    @JsonIgnore
    public void setTitle(String title) {
        Title = title;
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
    public String getCoverURL() {
        return CoverURL;
    }

    @JsonIgnore
    public void setCoverURL(String coverURL) {
        CoverURL = coverURL;
    }

    @JsonIgnore
    public String getStatus() {
        return Status;
    }

    @JsonIgnore
    public void setStatus(String status) {
        Status = status;
    }

    @JsonIgnore
    public String getCreationTime() {
        return CreationTime;
    }

    @JsonIgnore
    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    @JsonIgnore
    public String getMediaType() {
        return MediaType;
    }

    @JsonIgnore
    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }
}
