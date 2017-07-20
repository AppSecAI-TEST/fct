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
public class InviteCode implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 邀请码(8位字母+数字)
    /// </summary>
    private String code;

    /// <summary>
    /// 状态(0,未使用,1;已使用,-1:已失效)
    /// </summary>
    private Integer status;

    private Integer ownerId;

    private String ownerCellPhone;

    /// <summary>
    /// 使用者会员Id
    /// </summary>
    private Integer toMemberId;

    /// <summary>
    /// 使用者手机号码
    /// </summary>
    private String toCellPhone;

    /// <summary>
    /// 使用时间
    /// </summary>
    private Date useTime;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 失效时间
    /// </summary>
    private Date expireTime;

}
