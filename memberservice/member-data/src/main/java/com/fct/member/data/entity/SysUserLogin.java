package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserLogin implements Serializable{

    @Id
    private String token;

    private Integer userId;

    private String userName;

    private String cellPhone;

    private String ip;

    private Date expiredTime;

    private Date createTime;

}
