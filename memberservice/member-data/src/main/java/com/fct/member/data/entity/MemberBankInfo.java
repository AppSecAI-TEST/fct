package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/1.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberBankInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer memberId;

    private String cellPhone;

    private String name;

    private String bankName;

    private String bankAccount;

    /*是否审核*/
    private Integer status;

    private Date createTime;

}
