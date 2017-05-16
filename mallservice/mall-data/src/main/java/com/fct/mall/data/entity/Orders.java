package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Orders{

    @Id
    private String orderId;

    /// <summary>
    /// 会员
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 用户名
    /// </summary>
    private String userName;

    /// <summary>
    /// 店铺id
    /// </summary>
    private Integer shopId;

    /// <summary>
    /// 总积分
    /// </summary>
    private Integer points;

    /// <summary>
    /// 账户金额
    /// </summary>
    private BigDecimal accountAmount;

    /// <summary>
    /// 现金支付
    /// </summary>
    private BigDecimal cashAmount;

    /// <summary>
    /// 应付金额
    /// </summary>
    private BigDecimal payAmount;

    /// <summary>
    /// 订单总金额
    /// </summary>
    private BigDecimal totalAmount;

    /// <summary>
    /// 优惠券码
    /// </summary>
    private String couponCode;

    /// <summary>
    /// 状态
    /// </summary>
    private Integer status;

    /// <summary>
    /// 支付单号
    /// </summary>
    private String payOrderId;

    /// <summary>
    /// 支付公司名称
    /// </summary>
    private String payPlatform;

    /// <summary>
    /// 快递公司平台
    /// </summary>
    private String expressPlatform;

    /// <summary>
    /// 快递单号
    /// </summary>
    private String expressNO;

    /// <summary>
    /// 备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 下单时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 订单产品
    /// </summary>
    private List<OrderGoods> orderGoods;

    /// <summary>
    /// 订单收货信息
    /// </summary>
    private OrderReceiver orderReceiver;

    /// <summary>
    /// 订单相关时间，关闭时间、过期时间、发货时间...
    /// </summary>
    private OrderTime orderTime;
}
