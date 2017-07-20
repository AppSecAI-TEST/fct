package com.fct.message.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/4/7.
 */

@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MessageQueue implements Serializable {

    //会会Id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String sourceAppName;

    private String  typeId;

    private String targetModule;

    private String body;

    private String remark;

    private Date processTime;

    private Integer requestCount;

    private String failMessage;

    private Integer status;

    private Date createTime;
}