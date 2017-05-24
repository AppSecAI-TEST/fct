package com.fct.master.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table(name = "master_live")
public class MasterLive extends Base {

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

    @Column(name = "status")
    private int status;
}
