package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Orders implements Serializable{

    @Id
    private String orderId;

    /// <summary>
    /// 会员
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 手机号码
    /// </summary>
    private String cellPhone;

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
    /// 备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 订单产品
    /// </summary>
    @Transient
    private List<OrderGoods> orderGoods;

    /// <summary>
    /// 订单收货信息
    /// </summary>
    @Transient
    private OrderReceiver orderReceiver;

    //结算Id
    private Integer settleId;

    //评论状态{0：待评论，1.已评论}
    private Integer commentStatus;

    /// <summary>
    /// 支付时间
    /// </summary>
    private Date payTime;

    /// <summary>
    /// 下单时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 订单状态相关更新时间，关闭时间、过期时间、发货时间...
    /// </summary>
    private Date updateTime;

    /// <summary>
    /// 过期时间
    /// </summary>
    private Date expiresTime;

    //订单完成时间，默认为12天未确认，系统默认交易完成
    private Date finishTime;

    //冗余管理员操作者Id
    private String operatorId;
}
