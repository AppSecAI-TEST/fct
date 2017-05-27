package com.fct.member.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by jon on 2017/5/3.
 */

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberGrade implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 等级名称
    /// </summary>
    private String Name;

    /// <summary>
    /// 累积获得积分
    /// </summary>
    private Integer points;
}
