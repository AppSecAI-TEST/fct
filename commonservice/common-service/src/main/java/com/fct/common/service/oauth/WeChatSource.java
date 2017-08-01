package com.fct.common.service.oauth;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by z on 17-7-12.
 */
@Data
public class WeChatSource implements Serializable {

    private String accessToken;
    private Integer expireIn;
    private String refreshToken;
    private Long refreshTime;
}
