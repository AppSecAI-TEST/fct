package com.fct.pay.service;

import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.wxpay.WXPay;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by jon on 2017/4/22.
 */
@Service("mobilePayService")
public class MobilePayServiceImpl implements MobilePayService {

    public String wxpayWap(String payOrderNo, String openId, BigDecimal total_fee, String body,
                    String notifyUrl, String userIp, Date expireTime)
    {
        try {
            return WXPay.requestUnifiedOrderService(payOrderNo, openId, total_fee, body,notifyUrl,
                    userIp, expireTime);
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

    public String wxpayApp(String payOrderNo, BigDecimal total_fee, String body,
                    String callBackUrl, String notifyUrl, String createIP, Date expireTime)
    {
        return  "";
    }

    public PayNotify wxpayRefund(String payPlatform,String payOrderId,String refundId,
                                 BigDecimal payAmount,BigDecimal refundAmount)
    {
        try {
            return WXPay.requestRefundService(payPlatform, payOrderId, refundId, payAmount, refundAmount);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        return new PayNotify();
    }


}
