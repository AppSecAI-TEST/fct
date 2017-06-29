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

    PayNotify wxpayRefund(String payment,String payOrderId,String refundId,
                          BigDecimal payAmount,BigDecimal refundAmount);

}
