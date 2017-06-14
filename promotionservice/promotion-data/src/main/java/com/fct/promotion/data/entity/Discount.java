package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Discount implements Serializable {
    /// <summary>
    /// 主键
    /// </summary>
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 促销名称
    /// </summary>
    private String name;

    //广告图
    private String banner;

    //活动内容
    private String content;

    /// <summary>
    /// 审核状态  0：待审核， 1：已审核，2：已拒绝
    /// </summary>
    private Integer auditStatus;

    /// <summary>
    /// 开始时间
    /// </summary>
    private Date startTime;

    /// <summary>
    /// 结束时间
    /// </summary>
    private Date endTime;

    /// <summary>
    /// 活动未开始不能购买,秒杀
    /// </summary>
    private Integer notStartCanNotBuy;

    /// <summary>
    /// 会员等级以上才有资格参与抢购
    /// </summary>
    private Integer memberGradeId;

    /// <summary>
    /// 订单关闭时间
    /// </summary>
    private Integer orderCloseTime;

    /// <summary>
    /// 宝贝数量
    /// </summary>
    private Integer productCount;

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

    @Transient
    private List<DiscountProduct> productList;
}
