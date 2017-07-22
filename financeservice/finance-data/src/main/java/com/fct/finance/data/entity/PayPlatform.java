package com.fct.finance.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by jon on 2017/4/7.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PayPlatform implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /// <summary>
    /// 平台名称：支付宝APP支付、微信APP支付、微信H5支付、银联H5支付、银联app支付、线下支付
    /// </summary>
    private String name;

    /**
     * 前端使用该属性
     * */
    private String showName;

    /**
     * app,wap
     * */
    private String type;

    /**
     * website
     * */
    private String webSite;

    /// <summary>
    /// alipay_fctapp、wxpay_fctapp、wxpa_fctywap、unionpay_fctwap、unionpay_fctapp、offline
    /// </summary>
    private String code;

    /// <summary>
    /// 状态：{1:启用，0:禁用}
    /// </summary>
    private Integer status;

    /// <summary>
    /// 排序优先级
    /// </summary>
    private Integer sortIndex;
}
