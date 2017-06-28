package com.fct.message.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/12.
 */
@Configuration
public class QCloudSMSConfig {

    @Value("${qcloud.sms.appid}")
    private int appId;

    public int getAppId()
    {
        return appId;
    }

    public void setAppId(int appId)
    {
        this.appId = appId;
    }

    @Value("${qcloud.sms.appkey}")
    private String appKey;

    public String getAppKey()
    {
        return appKey;
    }

    public void setAppKey(String appKey)
    {
        this.appKey = appKey;
    }
}

