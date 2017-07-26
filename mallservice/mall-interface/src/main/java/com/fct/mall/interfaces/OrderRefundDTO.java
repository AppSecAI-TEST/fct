package com.fct.mall.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fct.mall.data.entity.OrderRefund;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by jon on 2017/6/13.
 */

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrderRefundDTO extends OrderRefund implements Serializable{

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

    //退款金额
    private BigDecimal payAmount;

    /**
     * 购买数量
     * */
    private Integer buyCount;
}