package com.fct.promotion.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jon on 2017/5/9.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountProductDTO implements Serializable {

    private Integer productId;

    private Discount discount;

    private DiscountProduct discountProduct;
}
