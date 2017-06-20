package com.fct.promotion.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountCouponDTO implements Serializable {

    private List<OrderProductDTO> discount;

    private CouponCodeDTO coupon;
}
