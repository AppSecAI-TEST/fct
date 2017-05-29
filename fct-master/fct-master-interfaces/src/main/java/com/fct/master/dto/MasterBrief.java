package com.fct.master.dto;

import java.io.Serializable;

/**
 * Created by nick on 2017/5/29.
 */
public class MasterBrief implements Serializable {

    private int masterId;

    private String coverUrl;

    private String brief;

    private String attentionCount;

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(String attentionCount) {
        this.attentionCount = attentionCount;
    }
}
