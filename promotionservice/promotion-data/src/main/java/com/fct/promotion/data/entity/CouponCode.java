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
 * Created by jon on 2017/5/9.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponCode implements Serializable {
    /// <summary>
    /// 主键
    /// </summary>
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer policyId;

    private Integer memberId;

    private String code;

    private Date createTime;

    private Date lastUpdateTime;

    private Date useTime;

    /// <summary>
    /// 券码状态  0：未使用，1：使用中，2：已使用，3：已过期
    /// </summary>
    private Integer status;
}
