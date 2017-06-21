package com.fct.common.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleCategory implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 宝贝分类名称
    /// </summary>
    private String name;

    /// <summary>
    /// 分类父Id(默认现一级0)
    /// </summary>
    private Integer parentId;

    /// <summary>
    /// 子父级符号1_10
    /// </summary>
    private String Code;

    /// <summary>
    /// 排序值
    /// </summary>
    private Integer sortIndex;
}

