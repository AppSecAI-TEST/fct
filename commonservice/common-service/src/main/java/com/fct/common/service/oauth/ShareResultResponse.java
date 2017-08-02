package com.fct.common.service.oauth;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jon on 2017/8/2.
 */
@Data
public class ShareResultResponse implements Serializable {

    private Integer errcode;

    private String errmsg;

    private String ticket;

    private String access_token;

    private String expires_in;
}
