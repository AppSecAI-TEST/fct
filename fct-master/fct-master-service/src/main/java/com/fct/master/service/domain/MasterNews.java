package com.fct.master.service.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fct.master.service.NewsType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 * 实时动态
 */
@Data
@Entity
@Table(name = "master_news")
public class MasterNews {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "news_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "news_type", length = 10)
    private String newsType;

    @Column(name = "imgs")
    private String imgs;

    @Column(name = "text", columnDefinition = "text")
    private String text;

    @Column(name = "vod_id")
    private String vodId;

    @Column(name = "del_flag")
    private int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
