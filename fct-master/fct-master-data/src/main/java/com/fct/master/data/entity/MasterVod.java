package com.fct.master.data.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by nick on 2017/5/24.
 * 大师点播
 */
@Data
@Entity
@Table(name = "master_vod")
public class MasterVod extends Base{

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
    @Column(name = "play_auth")
    private String playAuth;
}
