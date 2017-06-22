package com.fct.artist.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistDynamic {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * 合作艺人id
     * */
    private Integer artistId;

    /**
     * 动态内容
     * */
    private String content;

    /**
     * 图片，支持多图
     * */
    private String images;

    /**
     * 视频地址
     * */
    private String videoId;

    /**
     * 视频图片
     * */
    private String videoImg;

    /**
     * 状态
     * */
    private Integer status;

    /**
     * 创建时间
     * */
    private Date createTime;

}
