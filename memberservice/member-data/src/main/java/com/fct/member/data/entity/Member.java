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
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Member implements Serializable{

    //会会Id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private String password;

    private String cellPhone;

    private Integer gradeId;

    private Integer locked;

    private Integer authStatus;

    private Integer canInviteCount;

    private Integer inviterMemberId;

    private String inviterCellPhone;

    private Integer failLoginCount;

    private Integer loginCount;

    private Date loginTime;

    private Date registerTime;

}
