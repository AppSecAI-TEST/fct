package com.fct.common.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jon on 2017/7/27.
 */
@Data
@Configuration
public class QCloudConfig {

    @Value("${qcloud.vod.secretId}")
    private String secretId;

    @Value("${qcloud.vod.secretKey}")
    private String secretKey;

    @Value("${qcloud.vod.appId}")
    private long appId;
}