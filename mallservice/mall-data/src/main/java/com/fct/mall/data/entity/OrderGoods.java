package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderGoods {

    @Id
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
    private String image;

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
    /// 积分
    /// </summary>
    private Integer point;

    /// <summary>
    /// 应支付金额
    /// </summary>
    private BigDecimal payAmount;

    /// <summary>
    /// 总金额
    /// </summary>
    private BigDecimal totalAmount;

    /// <summary>
    /// 内容（快照）
    /// </summary>
    private String content;

    /// <summary>
    /// 状态（退货，换货。）
    /// </summary>
    private Integer status;
}
