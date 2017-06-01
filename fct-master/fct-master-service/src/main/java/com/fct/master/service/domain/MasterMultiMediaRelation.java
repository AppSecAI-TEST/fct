package com.fct.master.service.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by ningyang on 2017/6/1.
 */
@Entity
@Data
@Table(name = "master_multi_media_relations")
public class MasterMultiMediaRelation {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(length = 32, unique = true, nullable = false)
    private String id;

    @Column(length = 100)
    private String key;

    @Column(name = "master_id")
    private long masterId;

    @Column(name = "media_type", length = 20)
    private String mediaType;

    @Column(name = "img_url", length = 120)
    private String imgUrl;

    @Column(name = "vod_id")
    private String vodId;

    @Column(name = "del_flag")
    private int delFlag;

    @Column(name = "create_time")
    private Date createTime;
}
