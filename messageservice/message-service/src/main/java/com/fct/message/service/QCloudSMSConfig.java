package com.fct.message.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/12.
 */
@Configuration
public class QCloudSMSConfig {

    @Value("${qcloud.sms.appid}")
    private Integer appId;

    public Integer getAppId()
    {
        return appId;
    }

    public void setAppId(Integer appId)
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


    @Value("${qcloud.sms.sign}")
    private String sign;

    public String getSign()
    {
        return sign;
    }

    public void setSign(String sign)
    {
        this.sign = sign;
    }
}

