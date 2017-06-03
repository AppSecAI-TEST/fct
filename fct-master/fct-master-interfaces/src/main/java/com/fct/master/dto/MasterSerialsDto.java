package com.fct.master.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nick on 2017/5/27.
 */
public class MasterSerialsDto implements Serializable{

    private MasterBrief brief;

    private List<MasterNewsDto> masterNews;

    private MasterLiveDto masterLive;

    public MasterLiveDto getMasterLive() {
        return masterLive;
    }

    public void setMasterLive(MasterLiveDto masterLive) {
        this.masterLive = masterLive;
    }

    public MasterBrief getBrief() {
        return brief;
    }

    public void setBrief(MasterBrief brief) {
        this.brief = brief;
    }

    public List<MasterNewsDto> getMasterNews() {
        return masterNews;
    }

    public void setMasterNews(List<MasterNewsDto> masterNews) {
        this.masterNews = masterNews;
    }
}
