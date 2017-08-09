package com.fct.promotion.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/5/9.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductDTO implements Serializable {

    private Integer productId;

    private Integer count;

    /// <summary>
    /// 折后价
    /// </summary>
    private BigDecimal realPrice;

    private BigDecimal discountPrice;

    private Integer discountId;

    /// <summary>
    /// 规格ID
    /// </summary>
    private Integer sizeId;

    private Integer singleCount;

    /**
    * 活动开始时间
     * */
    private Date startTime;

    /**
     * 是否为秒杀商品
     * */
    private Integer notStartCanNotBuy;
}
