package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/12.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CouponUseLog implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String orderId;

    private Integer policyId;

    private String couponCode;

    private Date createTime;
}
