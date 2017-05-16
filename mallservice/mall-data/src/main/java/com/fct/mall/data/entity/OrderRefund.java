package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRefund {

    @Id
    private Integer id;

    /// <summary>
    /// 用户ID
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 订单号
    /// </summary>
    private String orderId;

    /// <summary>
    /// 商品Id
    /// </summary>
    private Integer goodsId;

    /// <summary>
    ///  商品规格
    /// </summary>
    private Integer goodsSpecId;

    /// <summary>
    /// 是否收到货
    /// </summary>
    private Integer isReceive;

    /// <summary>
    /// 是否退款
    /// </summary>
    private Integer isRefundMoney;

    /// <summary>
    /// 状态
    /// </summary>
    private Integer status;

    /// <summary>
    /// 退款方式
    /// </summary>
    private Integer refundMoneyType;

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
    private List<OrderRefundContent> refundContent;
}
