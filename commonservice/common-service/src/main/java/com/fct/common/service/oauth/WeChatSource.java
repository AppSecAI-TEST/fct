package com.fct.common.service.oauth;

import lombok.Data;

/**
 * Created by z on 17-7-12.
 */
@Data
public class WeChatSource {

    private String accessToken;
    private Integer expireIn;
    private String refreshToken;
    private Long refreshTime;
}
