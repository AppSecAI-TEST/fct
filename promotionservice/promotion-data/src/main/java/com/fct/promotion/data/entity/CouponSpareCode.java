package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/9.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponSpareCode {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private Date createTime;

    /// <summary>
    /// 更新日期
    /// </summary>
    private Date lastUpdateTime;

    /// <summary>
    /// 券码状态  0：未分配，1：分配中，2：已分配
    /// </summary>
    private Integer status;
}
