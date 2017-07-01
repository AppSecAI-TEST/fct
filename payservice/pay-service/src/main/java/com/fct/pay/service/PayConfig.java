package com.fct.pay.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/4/22.
 */

@Service
@ConfigurationProperties(prefix="pay") //application.yml中的pay下的属性
public class PayConfig {

    private Map<String, String> wxpay_fctwap = new HashMap<>();

    public Map<String, String> getWxpay_fctwap() {
        return wxpay_fctwap;
    }

    public void setWxpay_fctwap(Map<String, String> wxpay_fctwap) {
        this.wxpay_fctwap = wxpay_fctwap;
    }

    private Map<String, String> wxpay_fctapp = new HashMap<>();

    public Map<String, String> getWxpay_fctapp() {
        return wxpay_fctapp;
    }

    public void setWxpay_fctapp(Map<String, String> wxpay_fctapp) {
        this.wxpay_fctapp = wxpay_fctapp;
    }

    private Map<String, String> platform_ids = new HashMap<>();

    public Map<String, String> getPlatform_ids() {
        return platform_ids;
    }

    public void setPlatform_ids(Map<String, String> platform_ids) {
        this.platform_ids = platform_ids;
    }

    private Map<String,String> unionpay_fctwap = new HashMap<>();

    public Map<String, String> getUnionpay_fctwap() {
        return unionpay_fctwap;
    }

    public void setUnionpay_fctwap(Map<String, String> unionpay_fctwap)
    {
        this.unionpay_fctwap = unionpay_fctwap;
    }
}
