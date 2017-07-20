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
 * Created by jon on 2017/5/6.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class VerifyCode implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String sessionId;

    private String cellPhone;

    private String code;

    private Date expireTime;

    private Date createTime;
}
