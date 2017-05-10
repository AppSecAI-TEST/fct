package com.fct.promotion.interfaces.dto;

import com.fct.promotion.data.entity.CouponCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/5/9.
 */
public class CouponCodeDTO extends CouponCode {

    private String couponName;

    private Date startTime;

    private Date endTime;

    private BigDecimal fullAmount;

    private BigDecimal amount;

    private String productIds;
}
