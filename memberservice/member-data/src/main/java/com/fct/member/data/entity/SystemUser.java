package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/1.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemUser implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private String cellPhone;

    private String password;

    private Integer roleId;

    private Date loginTime;

    private Integer locked;

    private Date createTime;
}
