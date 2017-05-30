package com.fct.master.service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table(name = "master_live")
public class MasterLive {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name ="master_id")
    private Long masterId;

    //频道id
    @Column(name = "channel_id")
    private String channelId;
    //封面
    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "http_pull_url")//http拉流地址
    private String httpPullUrl;

    @Column(name = "http_push_url")//http推流地址
    private String httpPushUrl;

    @Column(name = "status")//0 是正常 正在直播 1 是断开或者停掉
    private int status;

    @Column(name = "del_flag")
    private int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}
