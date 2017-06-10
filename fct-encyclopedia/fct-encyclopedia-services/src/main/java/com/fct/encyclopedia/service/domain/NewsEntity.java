package com.fct.encyclopedia.service.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ningyang on 2017/6/10.
 */
@Entity
@Data
@Table(name = "news")
public class NewsEntity {

    @GeneratedValue(generator = "newsId")
    @GenericGenerator(name="newsId",strategy = "uuid")
    @Id
    @Column(name = "new_id", length = 32, nullable = false)
    private String newId;

    /**
     * 新闻标题
     */
    @Column(length = 40)
    private String title;

    /**
     * 分类id
     */
    @Column(name = "type_id", length = 32, nullable = false)
    private String typeId;

    /**
     * 新闻来源
     */
    @Column(name = "source")
    private String source;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 新闻事件
     */
    @Column(name = "news_date")
    private Date newsDate;

    /**
     * 状态 1为正常 0 非正常
     */
    @Column
    private String status;

    @Column(name = "content", columnDefinition = "text")
    private String content;
}
