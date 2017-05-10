package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/5/9.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountProduct {
    /// <summary>
    /// 主键
    /// </summary>
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer discountId;

    private Integer productId;

    private BigDecimal discountRate;

    private Integer singleCount;

    private Boolean isValidForSize;

    /// <summary>
    /// 创建人
    /// </summary>
    private Integer createUserId;

    /// <summary>
    /// 创建日期
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 最后修改人
    /// </summary>
    private Integer lastUpdateUserId;

    /// <summary>
    /// 更新日期
    /// </summary>
    private Date lastUpdateTime;

}
