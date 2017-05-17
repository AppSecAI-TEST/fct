package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by jon on 2017/5/1.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberInfo implements Serializable{
    @Id
    private Integer memberId;

    private String headPortrait;

    private String realName;

    private Integer sex;

    private String weixin;

    private String identityCardNo;

    private String identityCardImg;

}
