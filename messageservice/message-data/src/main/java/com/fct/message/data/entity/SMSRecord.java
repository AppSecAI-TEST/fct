package com.fct.message.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/6.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SMSRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String cellPhone;

    private String content;

    private String action;

    private String ip;

    private Integer count;

    /// <summary>
    /// 短信类型（0:普通，1：广告）
    /// </summary>
    private Integer type;

    /// <summary>
    /// 是否发送
    /// </summary>
    private int isSend;

    /// <summary>
    /// 发送时间
    /// </summary>
    private Date sendTime;

    /// <summary>
    /// 创建时间
    /// </summary>
    private Date createTime;

    /// <summary>
    /// 更新时间
    /// </summary>
    public Date updateTime;
}
