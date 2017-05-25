package com.fct.master.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nick on 2017/5/24.
 */
public class MasterDto implements Serializable{

    private Long masterId;

    private String masterName;

    private String coverUrl;

    //头衔
    private String title;

    //简介
    private String brief;

    private String profile;

    private Long worksCount;

    private Date updateTime;

    private int delflag;

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getWorksCount() {
        return worksCount;
    }

    public void setWorksCount(Long worksCount) {
        this.worksCount = worksCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDelflag() {
        return delflag;
    }

    public void setDelflag(int delflag) {
        this.delflag = delflag;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

}
