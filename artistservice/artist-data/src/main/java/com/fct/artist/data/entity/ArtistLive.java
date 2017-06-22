package com.fct.artist.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistLive implements Serializable {

    /**
     * 艺人Id
     * */
    @Id
    private Integer artistId;

    /**
     * 直播主题
     * */
    private String title;

    /**
     * 直播宣传图片
     * */
    private String banner;

    /**
     * 直播地址
     * */
    private String liveId;

    /**
     * 直播开始时间
     * */
    private Date startTime;

    /**
     * 直播结束时间
     * */
    private Date endTime;

    /**
     * 创建时间
     * */
    private Date createTime;

    /**
     * 审核状态{0:待审核,1:已审核}
     * */
    private Integer status;
}
