package com.fct.master.service.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fct.master.dto.MasterDto;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table(name = "master")
public class Master {

    public Master(){}

    public Master(MasterDto masterDto){
        this.masterName = masterDto.getMasterName();
        this.coverUrl = masterDto.getCoverUrl();
        this.title = masterDto.getTitle();
        this.brief = masterDto.getBrief();
        this.profile = masterDto.getProfile();
        if(createTime==null)
            createTime = new Date();
        this.updateTime = masterDto.getUpdateTime();
        this.delFlag = masterDto.getDelflag();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "master_id", nullable = false, unique = true)
    private Long masterId;

    @Column(name = "master_name", nullable = false)
    private String masterName;

    @Column(name = "cover_url")
    private String coverUrl;

    //头衔
    @Column(name = "title")
    private String title;

    //简介
    @Column(name = "brief")
    private String brief;

    @Column(name = "profile", length = 500)
    private String profile;

    @Column(name = "del_flag")
    private int delFlag; //逻辑删除 0 是不删除 1 是删除 默认是0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tb_master_goods", joinColumns = @JoinColumn(name = "master_id"), inverseJoinColumns = @JoinColumn(name = "goods_id"))
    List<Goods> goods;

}
