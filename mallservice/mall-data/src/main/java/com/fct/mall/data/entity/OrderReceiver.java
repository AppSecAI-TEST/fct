package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderReceiver {

    @Id
    private String orderId;

    /// <summary>
    /// 姓名
    /// </summary>
    private String name;

    /// <summary>
    /// 电话
    /// </summary>
    private String phone;

    /// <summary>
    /// 省份
    /// </summary>
    private String province;

    /// <summary>
    /// 城市
    /// </summary>
    private String city;

    /// <summary>
    /// 区域
    /// </summary>
    private String region;

    /// <summary>
    /// 地址
    /// </summary>
    private String address;

    /// <summary>
    /// 邮政编码
    /// </summary>
    private String postCode;
}
