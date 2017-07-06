package com.fct.member.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDTO implements Serializable {

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
}
