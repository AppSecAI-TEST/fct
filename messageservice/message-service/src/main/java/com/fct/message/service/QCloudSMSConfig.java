package com.fct.message.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/12.
 */
@Configuration
public class QCloudSMSConfig {

    @Value("${qcloud.sms.appid}")
    public static int appId;

    @Value("${qcloud.sms.appkey}")
    public static String appKey;
}

