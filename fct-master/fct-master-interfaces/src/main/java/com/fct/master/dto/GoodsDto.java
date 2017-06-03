package com.fct.master.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ningyang on 2017/6/3.
 */
public class GoodsDto {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(String artistIds) {
        this.artistIds = artistIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoImg() {
        return videoImg;
    }

    public void setVideoImg(String videoImg) {
        this.videoImg = videoImg;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getMultiImages() {
        return multiImages;
    }

    public void setMultiImages(String multiImages) {
        this.multiImages = multiImages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public Integer getAdvanceSaleDays() {
        return advanceSaleDays;
    }

    public void setAdvanceSaleDays(Integer advanceSaleDays) {
        this.advanceSaleDays = advanceSaleDays;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Integer getSellCount() {
        return sellCount;
    }

    public void setSellCount(Integer sellCount) {
        this.sellCount = sellCount;
    }

    public Integer getPayCount() {
        return payCount;
    }

    public void setPayCount(Integer payCount) {
        this.payCount = payCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public float getCommnetScore() {
        return commnetScore;
    }

    public void setCommnetScore(float commnetScore) {
        this.commnetScore = commnetScore;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
