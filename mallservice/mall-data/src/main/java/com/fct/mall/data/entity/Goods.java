package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/15.
 * I love nancy
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goods {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 分类
    /// </summary>
    private String categoryCode;

    //品级
    private Integer gradeId;

    /// <summary>
    /// 艺人(多个，逗号隔开)
    /// </summary>
    private String artistIds;

    /// <summary>
    /// 名称
    /// </summary>
    private String name;

    //副标题
    private String subTitle;

    /// <summary>
    /// 描述
    /// </summary>
    private String intro;

    //视频Id
    private String videoId;

    //视频图片
    private String videoImg;

    /// <summary>
    /// 市场价格
    /// </summary>
    private BigDecimal marketPrice;

    /// <summary>
    /// 销售价格
    /// </summary>
    private BigDecimal salePrice;

    /// <summary>
    /// 佣金
    /// </summary>
    private BigDecimal commission;

    /// <summary>
    /// 库存数量
    /// </summary>
    private Integer stockCount;

    /// <summary>
    /// 编码
    /// </summary>
    private String code;

    /// <summary>
    /// 商品二维条码
    /// </summary>
    private String barCode;

    /// <summary>
    /// 默认主图
    /// </summary>
    private String defaultImage;

    /// <summary>
    /// 多张图片
    /// </summary>
    private String multiImages;

    /// <summary>
    /// 内容
    /// </summary>
    private String content;

    //材质--泥料介绍
    private Integer materialId;

    //预售时间（天为单位，正常商品默认为0）
    private Integer advanceSaleDays;

    /// <summary>
    /// 状态(0:下架{待上架}，1:上架)
    /// </summary>
    private Integer status;

    /// <summary>
    /// 是否删除
    /// </summary>
    private Integer isDel;

    /// <summary>
    /// 排序
    /// </summary>
    private Integer sortIndex;

    /// <summary>
    /// 销售总数，付款成功为计算，如有退换货则相应减去
    /// </summary>
    private Integer sellCount;

    //已付款总笔数，如有退款则不减
    private Integer payCount;

    //累计评论总数
    private Integer commentCount;

    //评论分数；(描述分)
    private float commnetScore;

    ///浏览次数
    private Integer viewCount;

    /// <summary>
    /// 更新时间
    /// </summary>
    private Date updateTime;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 商品规格属性
    /// </summary>
    private List<GoodsSpecification> specification;
}