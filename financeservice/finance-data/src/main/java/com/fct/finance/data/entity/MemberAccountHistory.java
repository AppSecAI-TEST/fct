package com.fct.finance.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jon on 2017/4/7.
 */

@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MemberAccountHistory implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /// <summary>
    /// 用户Id
    /// </summary>
    private Integer memberId;

    private String cellPhone;

    /// <summary>
    /// 变动金额 (充值+提现)
    /// </summary>
    private BigDecimal amount;

    //充值金额(只做冗余不做展现)
    private  BigDecimal rechargeAmount;

    //可申请提现金额(只做冗余不做展现)
    private  BigDecimal withdrawAmount;

    /// <summary>
    /// 余额
    /// </summary>
    private BigDecimal balanceAmount;

    /// <summary>
    /// 变动积分
    /// </summary>
    private Integer points;

    /// <summary>
    /// 当前结余积分
    /// </summary>
    private Integer balancePoints;

    /// <summary>
    /// 行为类型(充值、消费、结算返佣、退款、提现)
    /// </summary>
    private  String tradeType;

    /// <summary>
    /// 相关数据主键
    /// </summary>
    private  String tradeId;

    /// <summary>
    /// 备注
    /// </summary>
    private String remark;

    /// <summary>
    /// 行为{0:支出,1:收入}
    /// </summary>
    private Integer behaviorType;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

}
