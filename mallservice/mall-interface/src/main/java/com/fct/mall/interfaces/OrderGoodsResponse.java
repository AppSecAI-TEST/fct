package com.fct.mall.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fct.mall.data.entity.OrderGoods;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderGoodsResponse implements Serializable {

    private List<OrderGoods> items;

    private String couponCode;

    private BigDecimal couponAmount;
}
