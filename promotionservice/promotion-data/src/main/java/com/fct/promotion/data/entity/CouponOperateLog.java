package com.fct.promotion.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/12.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponOperateLog implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String typeName;

    private String relationId;

    private String oldContent;

    private String newContent;

    private Integer operateId;

    private Date operateTime;
}