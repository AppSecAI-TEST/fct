package com.fct.source.data.entity;

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
public class ImageCategory implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 宝贝分类名称
    /// </summary>
    private String name;

    /// <summary>
    /// 排序值
    /// </summary>
    private Integer sortIndex;

}