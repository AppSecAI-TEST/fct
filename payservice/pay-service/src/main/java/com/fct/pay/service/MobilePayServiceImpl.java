package com.fct.pay.service;

import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.wxpay.WXPay;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by jon on 2017/4/22.
 */
@Service("mobilePayService")
public class MobilePayServiceImpl implements MobilePayService {

    @Autowired
    private WXPay wxPay;

    public String wxpayWap(String payment,String payOrderNo, String openId, BigDecimal total_fee, String body,
                    String notifyUrl, String userIp, Date expireTime)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctwap";
        }
        try {
            return wxPay.requestUnifiedOrderService(payment,payOrderNo, openId, total_fee, body,notifyUrl,
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
        return wxPay.payNotify(mapParam,xmlContent);
    }

    public String wxpayApp(String payment,String payOrderNo, BigDecimal total_fee, String body,
                    String callBackUrl, String notifyUrl, String createIP, Date expireTime)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctapp";
        }
        try {
            return wxPay.requestAppPay(payment, payOrderNo, total_fee, body, notifyUrl, createIP, expireTime);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            return "";
        }
    }

    public PayNotify wxpayRefund(String payment,String payOrderId,String refundId,
                                 BigDecimal payAmount,BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctwap";
        }
        try {
            return wxPay.requestRefundService(payment, payOrderId, refundId, payAmount, refundAmount);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        return new PayNotify();
    }


}
