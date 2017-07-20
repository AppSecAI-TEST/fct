package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrderRefundMessage implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 退款编号
    /// </summary>
    private Integer refundId;

    /// <summary>
    /// 系统用户操作者Id
    /// </summary>
    private Integer operatorId;

    /// <summary>
    /// 说明
    /// </summary>
    private String description;

    /// <summary>
    /// 上传图片
    /// </summary>
    private String images;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;
}
