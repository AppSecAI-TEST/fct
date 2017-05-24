package com.fct.master.data.entity;

import com.fct.master.dto.MasterDto;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 */
@Data
@Entity
@Table
public class Master extends Base{

    public Master(){}

    public Master(MasterDto masterDto){
        this.masterName = masterDto.getMasterName();
        this.coverUrl = masterDto.getCoverUrl();
        this.title = masterDto.getTitle();
        this.brief = masterDto.getBrief();
        this.profile = masterDto.getProfile();
        this.worksCount = masterDto.getWorksCount();
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

    @Column(name = "works_count")
    private Long worksCount;

}
