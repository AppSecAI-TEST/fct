package com.fct.master.dto;

import java.util.List;

/**
 * Created by nick on 2017/5/24.
 */
public class MasterResponse {

    private int current;

    private List<MasterDto> masters;

    private int allCount;

    private boolean hasMore;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<MasterDto> getMasters() {
        return masters;
    }

    public void setMasters(List<MasterDto> masters) {
        this.masters = masters;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
