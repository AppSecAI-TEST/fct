package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderTime {

    @Id
    private String orderId;

    /// <summary>
    /// 支付时间
    /// </summary>
    private Date payTime;

    /// <summary>
    /// 取消时间
    /// </summary>
    private Date cancelTime;

    /// <summary>
    /// 发货时间
    /// </summary>
    private Date sendTime;

    /// <summary>
    /// 完成时间
    /// </summary>
    private Date finshTime;

    /// <summary>
    /// 过期时间
    /// </summary>
    private Date expiresTime;
}
