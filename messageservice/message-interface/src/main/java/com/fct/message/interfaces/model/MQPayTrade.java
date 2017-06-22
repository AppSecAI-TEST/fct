package com.fct.message.interfaces.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
@Data
public class MQPayTrade {
    /// <summary>
    /// 支付Id
    /// </summary>
    private String pay_orderid;

    /// <summary>
    /// 订单状态
    /// </summary>
    private Integer trade_status;

    /// <summary>
    /// 支付业务类型
    /// </summary>
    private String trade_type;

    /// <summary>
    /// 支付业务id
    /// </summary>
    private String trade_id;

    /// <summary>
    /// 备注
    /// </summary>
    private String desc;

    private List<MQPayRefund> refund;
}
