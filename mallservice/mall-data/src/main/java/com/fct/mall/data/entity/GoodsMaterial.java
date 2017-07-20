package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/17.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class GoodsMaterial implements Serializable{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String name;

    ///材质类型{0:泥料,1:术语,2:..}
    private Integer typeid;

    private String images;

    /**
     * 简单介绍
     * */
    private String intro;

    private String description;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
