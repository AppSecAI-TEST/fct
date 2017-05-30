package com.fct.master.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nick on 2017/5/27.
 */
public class MasterLiveDto implements Serializable {

    private Long id;

    private Long masterId;

    //频道id
    private String channelId;

    //封面
    private String coverUrl;

    private String httpPullUrl;

    //http推流地址
    private String httpPushUrl;

    //0 是正常 正在直播 1 是断开或者停掉
    private int status;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getHttpPullUrl() {
        return httpPullUrl;
    }

    public void setHttpPullUrl(String httpPullUrl) {
        this.httpPullUrl = httpPullUrl;
    }

    public String getHttpPushUrl() {
        return httpPushUrl;
    }

    public void setHttpPushUrl(String httpPushUrl) {
        this.httpPushUrl = httpPushUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
