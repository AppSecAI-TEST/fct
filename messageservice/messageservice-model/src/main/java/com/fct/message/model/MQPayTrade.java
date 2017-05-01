package com.fct.message.model;

import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
public class MQPayTrade {
    /// <summary>
    /// 支付Id
    /// </summary>
    public String pay_orderid;

    /// <summary>
    /// 订单状态
    /// </summary>
    public Integer trade_status;

    /// <summary>
    /// 支付业务类型
    /// </summary>
    public String trade_type;

    /// <summary>
    /// 支付业务id
    /// </summary>
    public String trade_id;

    /// <summary>
    /// 备注
    /// </summary>
    public String desc;

    public List<MQPayRefund> refund;
}
