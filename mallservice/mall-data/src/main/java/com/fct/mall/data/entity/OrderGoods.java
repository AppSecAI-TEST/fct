package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderGoods implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private Integer goodsId;

    /// <summary>
    /// 商品规格Id
    /// </summary>
    private Integer goodsSpecId;

    /// <summary>
    /// 名称
    /// </summary>
    private String name;

    /// <summary>
    /// 规格名称
    /// </summary>
    private String specName;

    /// <summary>
    /// 图片
    /// </summary>
    private String img;

    /// <summary>
    /// 数量
    /// </summary>
    private Integer buyCount;

    /// <summary>
    /// 价格
    /// </summary>
    private BigDecimal price;

    /// <summary>
    /// 佣金
    /// </summary>
    private BigDecimal commission;

    /// <summary>
    /// 促销价格
    /// </summary>
    private BigDecimal promotionPrice;

    /// <summary>
    /// 优惠券面额
    /// </summary>
    private BigDecimal couponAmount;

    /// <summary>
    /// 应支付金额(promotionPrice-couponAmount)*buyCount
    /// </summary>
    private BigDecimal payAmount;

    /// <summary>
    /// 总金额(price*buycount)
    /// </summary>
    private BigDecimal totalAmount;

    /// <summary>
    /// 内容（快照）
    /// </summary>
    private String content;
}
