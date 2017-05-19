package com.fct.thridparty.vod.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by nick on 2017/5/19.
 */
@Data
public class VodResponse {
    @JsonProperty("RequestId")
    private String RequestId;
    @JsonProperty("HostId")
    private String HostId;
    @JsonProperty("Code")
    private String Code;
    @JsonProperty("Message")
    private String Message;
}
