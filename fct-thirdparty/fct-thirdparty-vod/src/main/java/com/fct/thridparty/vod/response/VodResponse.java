package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
public class VodResponse {

    @JsonProperty
    private String RequestId;

    @JsonProperty
    private String HostId;

    @JsonProperty
    private String Code;

    @JsonProperty
    private String Message;

    @JsonIgnore
    public String getHostId() {
        return HostId;
    }

    @JsonIgnore
    public void setHostId(String hostId) {
        HostId = hostId;
    }

    @JsonIgnore
    public String getCode() {
        return Code;
    }

    @JsonIgnore
    public void setCode(String code) {
        Code = code;
    }

    @JsonIgnore
    public String getMessage() {
        return Message;
    }

    @JsonIgnore
    public void setMessage(String message) {
        Message = message;
    }
}
