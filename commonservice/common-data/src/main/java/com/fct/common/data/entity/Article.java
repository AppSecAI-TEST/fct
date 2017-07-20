package com.fct.common.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * banner
     */
    private String banner;

    /**
     * 分类id
     */
    private String categoryCode;

    /**
     * 简单介绍
     */
    private String intro;

    /**
     * 新闻来源
     */
    private String source;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer viewCount;

    /**
     * 状态 1为正常 0 非正常
     */
    private Integer status;

    private String content;

    /**
     * 排序值
     */
    private Integer sortIndex;
}
