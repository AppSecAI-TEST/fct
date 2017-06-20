package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponPolicy implements Serializable {
    /// <summary>
    /// 主键
    /// </summary>
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 优惠卷名称
    /// </summary>
    private String name;

    /// <summary>
    /// 优惠卷类型  0：全场通用  1：商品优惠券
    /// </summary>
    private Integer typeId;

    /// <summary>
    /// 面额
    /// </summary>
    private BigDecimal amount;

    /// <summary>
    /// 使用条件 0：无，1：满XX元使用
    /// </summary>
    private Integer usingType;

    /// <summary>
    /// 满XX元使用
    /// </summary>
    private BigDecimal fullAmount;

    /// <summary>
    /// 审核状态  0：待审核， 1：已审核，2：已拒绝
    /// </summary>
    private Integer auditStatus;

    /// <summary>
    /// 0：用户领取，1：系统生成
    /// </summary>
    private Integer fetchType;

    /// <summary>
    /// 开始时间
    /// </summary>
    private Date startTime;

    /// <summary>
    /// 结束时间
    /// </summary>
    private Date endTime;

    /// <summary>
    /// 发行量
    /// </summary>
    private Integer totalCount;

    /// <summary>
    /// 每人领取的数量
    /// </summary>
    private Integer singleCount;

    /// <summary>
    /// 已领取数量
    /// </summary>
    private Integer receivedCount;

    /// <summary>
    /// 券码生成状态(针对系统发放)   0 待生成，1，已生成
    /// </summary>
    private Integer generateStatus;

    /// <summary>
    /// 产品ID
    /// </summary>
    private String productIds;

    /// <summary>
    /// 创建人
    /// </summary>
    private int createUserId;

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
