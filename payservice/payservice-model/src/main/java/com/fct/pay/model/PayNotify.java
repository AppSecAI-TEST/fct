package com.fct.pay.model;

import java.util.Map;

/**
 * Created by jon on 2017/4/24.
 */
public class PayNotify {

    /// <summary>
    /// 支付平台
    /// </summary>
    private String payPlatform;

    public String getPayPlatform() {
        return payPlatform;
    }

    public void setPayPlatform(String payPlatform) {
        this.payPlatform = payPlatform;
    }

    /// <summary>
    /// 支付订单号
    /// </summary>
    private String payOrderNo;

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    /// <summary>
    /// 请求类型
    /// </summary>
    private String requestType;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    /// <summary>
    /// 支付平台处理请求的过程中是否出错
    /// </summary>
    private Boolean hasError;

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }


    /// <summary>
    /// 支付平台处理请求的过程中出错时的错误信息
    /// </summary>
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /// <summary>
    /// 扩展属性，包含汇付所有的返回信息
    /// </summary>
    private Map<String, Object> extandProperties;

    public Map<String, Object> getExtandProperties() {
        return extandProperties;
    }

    public void setExtandProperties(Map<String, Object> extandProperties) {
        this.extandProperties = extandProperties;
    }


}
