package com.fct.pay.interfaces;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by jon on 2017/4/24.
 */
public interface MobilePayService {

    String wxpayWap(String payment,String payOrderNo, String openId, BigDecimal total_fee, String body,
                    String notifyUrl, String userIp, Date expireTime);

    PayNotify wxpayNotify(Map<String, String> dicParam, String xmlContent);

    String wxpayApp(String payment,String payOrderNo, BigDecimal total_fee, String body,
                    String callBackUrl, String notifyUrl, String createIP, Date expireTime);

    PayNotify wxpayWapRefund(String payment,String payOrderId,String refundId,
                          BigDecimal payAmount,BigDecimal refundAmount);

    PayNotify wxpayAppRefund(String payment,String payOrderId,String refundId,
                             BigDecimal payAmount,BigDecimal refundAmount);

    String unionpayWap(String payment,String orderId, BigDecimal payAmount, String desc, Date expireTime,
                           String notifyUrl, String callbackUrl);

    PayNotify unionpayCallBack(Map<String, String> map);

    PayNotify unionpayNotify(Map<String, String> map);

    PayNotify unionpayRefundNotify(Map<String, String> map);

    PayNotify unionpayWapRefund(String payment,String refundId, String payOrderId, BigDecimal payAmount, BigDecimal refundAmount);

    PayNotify unionpayAppRefund(String payment,String refundId, String payOrderId, BigDecimal payAmount, BigDecimal refundAmount);

    String alipayApp(String payment, String payOrderNo, BigDecimal amount, String title, Date expireTime,
                     String notifyUrl);

    PayNotify alipayAppNotify(Map<String, String> map);

}
