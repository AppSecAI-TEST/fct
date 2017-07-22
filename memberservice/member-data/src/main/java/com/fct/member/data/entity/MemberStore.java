package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jon on 2017/5/1.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MemberStore implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer memberId;

    private String cellPhone;

    private String name;

    private String desc;

    private Integer status;

    private Date createTime;

    private Integer inviterMemberId;

    private String inviterCellPhone;

}
