package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/1.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberLogin {

    @Id
    private String token;

    /// <summary>
    /// 会员Id
    /// </summary>
    private Integer memberId;

    /// <summary>
    /// 用户名称
    /// </summary>
    private String userName;

    /// <summary>
    /// 电话
    /// </summary>
    private String cellPhone;

    /// <summary>
    /// 等级Id
    /// </summary>
    private Integer gradeId;

    /// <summary>
    /// 用户头像
    /// </summary>
    private String headPortrait;

    /// <summary>
    /// 店铺Id
    /// </summary>
    private Integer shopId;

    /// <summary>
    /// 实名认证
    /// </summary>
    private Integer authStatus;

    /// <summary>
    /// 邀请者会员Id
    /// </summary>
    private Integer inviterId;

    /// <summary>
    /// 登陆ip
    /// </summary>
    private String ip;

    /// <summary>
    /// 登陆时间
    /// </summary>
    private Date loginTime;

    /// <summary>
    /// 过期时间
    /// </summary>
    private Date expireTime;
}
