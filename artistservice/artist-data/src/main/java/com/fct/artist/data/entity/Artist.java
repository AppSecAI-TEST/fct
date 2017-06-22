package com.fct.artist.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artist implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * 艺人名称
     * */
    private String name;

    /**
    * 艺人头衔
    * */
    private String title;

    /**
     * 主页宣传图
     * */
    private String mainImg;

    /**
     * 艺人个人主页顶部图片
     * */
    private String banner;

    /**
     * 头像
     * */
    private String headPortrait;

    /**
     * 介绍
     * */
    private String intro;

    /**
     * 详细描述
     * */
    private String Description;

    /**
     * 状态{0:待审核,1:已审核}
     * */
    private Integer status;

    /**
     * 浏览次数
     * */
    private Integer viewCount;

    /**
     * 关注数
     * */
    private Integer followCount;

    /**
     * 作品数量
     * */
    private Integer goodsCount;

    /**
     * 更新时间
     * */
    private Date updateTime;

    /**
     * 作品
     * **/
    @Transient
    List<ArtistGoods> goods;

    /**
     * 直播
     * */
    @Transient
    ArtistLive live;
}
