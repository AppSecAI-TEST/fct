package com.fct.pay.service;

import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.model.PayNotify;
import com.fct.pay.service.wxpay.WXPay;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by jon on 2017/4/22.
 */
public class MobilePayServiceImpl implements MobilePayService {

    public String wxpayWap(String payment, String payOrderNo, String openId, BigDecimal total_fee, String body,
                    String notifyUrl, String userIp, Integer expireMinutes)
    {
        try {
            return WXPay.requestUnifiedOrderService(payment, payOrderNo, openId, total_fee, body,notifyUrl,
                    userIp, expireMinutes);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            return "";
        }
    }

    public PayNotify wxpayNotify(Map<String, String> mapParam, String xmlContent)
    {
        return WXPay.payNotify(mapParam,xmlContent);
    }

    public String wxpayApp(String payment, String payOrderNo, BigDecimal total_fee, String body,
                    String callBackUrl, String notifyUrl, String createIP, Integer expireMinutes)
    {
        return  "";
    }

}
