package com.fct.mall.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by jon on 2017/5/15.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderComment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer memberId;

    //商品+订单号=唯一
    private Integer GoodsId;

    private String orderId;

    private String content;

    //回复内容
    private String replyContent;

    //晒图
    private String picture;

    //描述相符：失望1、不满2、一般3、满意4、惊喜5
    private Integer descScore;

    //卖家服务
    private Integer saleScore;

    //物流服务
    private Integer logisticsScore;

    private Date createTime;

    private Date updateTime;
}
