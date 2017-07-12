package com.fct.common.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by z on 17-7-12.
 */

@Data
@Configuration
public class OAuthCofnig {

    @Value("${oauth.wechat.appId}")
    private String appId;

    @Value("${oauth.wechat.appSecret}")
    private String appSecret;
}
