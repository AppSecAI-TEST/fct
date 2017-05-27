package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/17.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsMaterial {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String images;

    private String desc;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
