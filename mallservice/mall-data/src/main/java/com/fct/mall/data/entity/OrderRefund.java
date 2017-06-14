package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRefund implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 用户ID
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 订单号
    /// </summary>
    private String orderId;

    //订单商品Id
    private Integer orderGoodsId;

    /// <summary>
    /// 是否收到货
    /// </summary>
    private Integer isReceived;

    /// <summary>
    /// 服务类型{0:仅退款,1:退货退款}
    /// </summary>
    private Integer serviceType;

    //退款原因
    private String refundReason;

    /// <summary>
    /// 状态
    /// </summary>
    private Integer status;

    /// <summary>
    /// 退款方式
    /// </summary>
    private Integer refundMethod;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 修改时间
    /// </summary>
    private Date updateTime;

    /// <summary>
    /// 修改时间
    /// </summary>
    @Transient
    private List<OrderRefundMessage> refundMessage;

    @Transient
    private OrderGoods orderGoods;
}
