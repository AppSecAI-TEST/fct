package com.fct.message.model;

import java.math.BigDecimal;

/**
 * Created by jon on 2017/4/11.
 */
public class MQPaySuccess {

    /// <summary>
    /// 支付号
    /// </summary>
    public String pay_orderid;

    /// <summary>
    /// 交易类型
    /// </summary>
    public String trade_type;

    /// <summary>
    /// 交易Id
    /// </summary>
    public String trade_id;

    /// <summary>
    /// 支付状态{支付成功、支付余额异常}
    /// 业务方接受支付成功状态后，如业务正常进行则通知支付方成功，否则告知交易失败发起退款请求
    /// 业务方接受支付余额异常状态，将业务状态更新为交易关闭，并发起退款请求。
    /// </summary>
    public String pay_status;

    /// <summary>
    /// 支付时间
    /// </summary>
    public String pay_time;

    /// <summary>
    /// 帐户支付金额
    /// </summary>
    public BigDecimal account_amount;

    /// <summary>
    /// 银行支付金额
    /// </summary>
    public BigDecimal pay_amount;

    /// <summary>
    /// 支付平台方式
    /// </summary>
    public String pay_platform;

    /// <summary>
    /// 支付所用积分
    /// </summary>
    public Integer points;

    /// <summary>
    /// 折扣金额
    /// </summary>
    public BigDecimal discount_amount;

    /// <summary>
    /// 总金额
    /// </summary>
    public BigDecimal total_amount;

    /// <summary>
    /// 备注
    /// </summary>
    public String remark;

    /// <summary>
    /// 异步通知地址
    /// </summary>
    public String notify_url;
}
