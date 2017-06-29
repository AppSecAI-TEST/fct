package com.fct.pay.service;

import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.unionpay.UnionPayManager;
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

    @Autowired
    private UnionPayManager unionPayManager;

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

    public PayNotify wxpayWapRefund(String payment,String payOrderId,String refundId,
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

    public PayNotify wxpayAppRefund(String payment,String payOrderId,String refundId,
                             BigDecimal payAmount,BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctapp";
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

    public String unionpayWap(String payment,String orderId, BigDecimal payAmount, String desc, Date expireTime,
                                  String notifyUrl, String callbackUrl)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "unionpay_fctwap";
        }
        return unionPayManager.createWapPayUrl(payment,orderId,payAmount,desc,expireTime,notifyUrl,callbackUrl);
    }

    public PayNotify unionpayCallBack(Map<String, String> map)
    {
        return unionPayManager.callBack(map);
    }

    public PayNotify unionpayNotify(Map<String, String> map)
    {
        return unionPayManager.notify(map);
    }

    public PayNotify unionpayRefundNotify(Map<String, String> map)
    {
        return unionPayManager.refundNotify(map);
    }

    public PayNotify unionpayWapRefund(String payment,String refundId, String payOrderId, BigDecimal payAmount, BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "unionpay_fctwap";
        }

        return unionPayManager.refund(payment,refundId,payOrderId,payAmount,refundAmount);
    }

    public PayNotify unionpayAppRefund(String payment,String refundId, String payOrderId, BigDecimal payAmount, BigDecimal refundAmount)
    {
        return null;
    }

    public String alipayApp(String payment, String payOrderNo, BigDecimal amount, String title, Date expireTime,
                     String notifyUrl)
    {
        return "";
    }

    public PayNotify alipayAppNotify(Map<String, String> map)
    {
        return null;
    }
}
