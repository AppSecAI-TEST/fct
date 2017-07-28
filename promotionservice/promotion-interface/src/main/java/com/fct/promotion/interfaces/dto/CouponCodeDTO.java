package com.fct.promotion.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fct.promotion.data.entity.CouponCode;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/5/9.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponCodeDTO extends CouponCode implements Serializable {

    private String couponName;

    private Date startTime;

    private Date endTime;

    private BigDecimal fullAmount;

    private BigDecimal amount;

    private String productIds;

    private Integer typeId;
}
