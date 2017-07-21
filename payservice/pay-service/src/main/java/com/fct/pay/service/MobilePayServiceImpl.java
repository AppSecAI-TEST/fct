package com.fct.pay.service;

import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.alipay.AlipayManager;
import com.fct.pay.service.unionpay.UnionPayManager;
import com.fct.pay.service.wxpay.WxpayManager;
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
    private WxpayManager wxpayManager;

    @Autowired
    private UnionPayManager unionPayManager;

    @Autowired
    private AlipayManager alipayManager;

    public String wxpayWap(String payment,String payOrderNo, String openId, BigDecimal total_fee, String body,
                    String notifyUrl, String userIp, Date expireTime)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctwap";
        }
        try {
            return wxpayManager.requestUnifiedOrderService(payment, payOrderNo, openId, total_fee, body, notifyUrl,
                    userIp, expireTime);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "";
    }

    public PayNotify wxpayNotify(Map<String, String> mapParam, String xmlContent)
    {
        try {
            return wxpayManager.payNotify(mapParam, xmlContent);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public String wxpayApp(String payment,String payOrderNo, BigDecimal total_fee, String body,
                    String callBackUrl, String notifyUrl, String createIP, Date expireTime)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctapp";
        }
        try {
            return wxpayManager.requestAppPay(payment, payOrderNo, total_fee, body, notifyUrl, createIP, expireTime);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "";
    }

    public PayNotify wxpayWapRefund(String payment,String payOrderId,String refundId,
                                 BigDecimal payAmount,BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctwap";
        }
        try {
            return wxpayManager.requestRefundService(payment, payOrderId, refundId, payAmount, refundAmount);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify wxpayAppRefund(String payment,String payOrderId,String refundId,
                             BigDecimal payAmount,BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "wxpay_fctapp";
        }
        try {
            return wxpayManager.requestRefundService(payment, payOrderId, refundId, payAmount, refundAmount);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;

    }

    public String unionpayWap(String payment,String orderId, BigDecimal payAmount, String desc, Date expireTime,
                                  String notifyUrl, String callbackUrl)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "unionpay_fctwap";
        }
        try {
            return unionPayManager.createWapPayUrl(payment,orderId,payAmount,desc,expireTime,notifyUrl,callbackUrl);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "";
    }

    public PayNotify unionpayCallBack(Map<String, String> map)
    {
        try {
            return unionPayManager.callBack(map);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify unionpayNotify(Map<String, String> map)
    {
        try {
            return unionPayManager.notify(map);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify unionpayRefundNotify(Map<String, String> map)
    {
        try {
            return unionPayManager.refundNotify(map);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify unionpayWapRefund(String payment,String refundId, String payOrderId, BigDecimal payAmount, BigDecimal refundAmount)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "unionpay_fctwap";
        }
        try {
            return unionPayManager.refund(payment,refundId,payOrderId,payAmount,refundAmount);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
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

    public String alipayWap(String payment, String payOrderNo, BigDecimal amount, String title, Date expireTime,
                     String notifyUrl)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "alipay_fct";
        }
        try {
            return alipayManager.createWapPayUrl(payment,payOrderNo,title,amount,expireTime);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify alipayNotify(Map<String, String> map)
    {
        try {
            return alipayManager.notify(map);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify alipayCallback(Map<String, String> map)
    {
        try {
            return alipayManager.callback(map);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PayNotify refund(String payment,String payOrderId,String refundId,BigDecimal refundAmount,
                            String refundReason)
    {
        if(StringUtils.isEmpty(payment))
        {
            payment = "alipay_fct";
        }
        try {
            return alipayManager.refund(payment,payOrderId,refundId,refundAmount,refundReason);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}
