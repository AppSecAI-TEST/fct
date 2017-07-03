package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberFavourite {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer memberId;

    /***
     * 收藏类型 {0:宝贝,1:大师}
     * */
    private Integer favType;

    private Integer relatedId;

    private Date createTime;
}
