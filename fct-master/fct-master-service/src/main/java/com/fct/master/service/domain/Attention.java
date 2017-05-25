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
@Table(name = "attention")
public class Attention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_id")
    private Long masterId;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "del_flag")
    private int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}
