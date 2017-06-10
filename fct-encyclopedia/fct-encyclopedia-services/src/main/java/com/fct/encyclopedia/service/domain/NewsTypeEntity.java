package com.fct.encyclopedia.service.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by ningyang on 2017/6/10.
 */
@Data
@Entity
@Table(name = "news_type")
public class NewsTypeEntity {

    @Id
    @GeneratedValue(generator = "newsTypeId")
    @GenericGenerator(name="newsTypeId",strategy = "uuid")
    @Column(length = 32, nullable = false)
    private String id;

    /**
     * 新闻分类类型
     */
    @Column(name = "type_name", length = 100, nullable = false)
    private String typeName;

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
     * 删除标识 1 删除 0 未删除
     */
    @Column(name = "del_flag")
    private String delFlag;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "news_type_relation", joinColumns = @JoinColumn(name = "type_id"), inverseJoinColumns = @JoinColumn(name = "new_id"))
    List<NewsEntity> news;
}
