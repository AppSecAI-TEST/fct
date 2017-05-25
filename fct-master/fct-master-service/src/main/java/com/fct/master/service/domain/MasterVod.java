package com.fct.master.service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 * 大师点播
 */
@Data
@Entity
@Table(name = "master_vod")
public class MasterVod {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_id")
    private Long master_id;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "vod_id")
    private String vodId;

    /**
     * 播放凭证
     */
    @Column(name = "play_auth", length = 32)
    private String playAuth;

    @Column(name = "del_flag")
    private int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
