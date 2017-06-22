package com.fct.message.interfaces.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by jon on 2017/4/11.
 */
@Data
public class MQPayRefund {

    /// <summary>
    /// 退款交易id
    /// </summary>
    public String trade_id;

    /// <summary>
    /// 退款交易类型
    /// </summary>
    public String trade_type;

    /// <summary>
    /// 退款方式
    /// </summary>
    public Integer method;

    /// <summary>
    /// 支付平台方式
    /// </summary>
    public String pay_platform;

    /// <summary>
    /// 支付订单号
    /// </summary>
    public String pay_orderid;

    /// <summary>
    /// 支付金额
    /// </summary>
    public BigDecimal pay_amount;

    /// <summary>
    /// 退款订单号
    /// </summary>
    public Integer refund_id;

    /// <summary>
    /// 退款金额
    /// </summary>
    public BigDecimal refund_amount;

    /// <summary>
    /// 账户金额
    /// </summary>
    public BigDecimal account_amount;

    /// <summary>
    /// 现金金额
    /// </summary>
    public BigDecimal cash_amount;

    /// <summary>
    /// 退款积分
    /// </summary>
    public Integer points;

    /// <summary>
    /// 备注
    /// </summary>
    public String desc;
}
