package com.fct.master.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nick on 2017/5/27.
 */
public class MasterSerialsDto implements Serializable{

    private String brief;

    private String coverUrl;

    private List<MasterNewsDto> masterNews;

    private MasterLiveDto masterLive;


    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<MasterNewsDto> getMasterNews() {
        return masterNews;
    }

    public void setMasterNews(List<MasterNewsDto> masterNews) {
        this.masterNews = masterNews;
    }

    public MasterLiveDto getMasterLive() {
        return masterLive;
    }

    public void setMasterLive(MasterLiveDto masterLive) {
        this.masterLive = masterLive;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
