package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ShoppingCart implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 会员Id
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 店铺ID
    /// </summary>
    private Integer shopId;

    /// <summary>
    /// 商品Id
    /// </summary>
    private Integer goodsId;

    /// <summary>
    /// 规格Id
    /// </summary>
    private Integer goodsSpecId;

    /// <summary>
    /// 数量
    /// </summary>
    private Integer buyCount;

    /// <summary>
    /// 加入时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 更新时间
    /// </summary>
    private Date updateTime;

    /// <summary>
    /// 产品信息
    /// </summary>
    @Transient
    private OrderGoods goods;
}
