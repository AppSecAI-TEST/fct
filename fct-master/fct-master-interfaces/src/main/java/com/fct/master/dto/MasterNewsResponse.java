package com.fct.master.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ningyang on 2017/5/30.
 */
public class MasterNewsResponse implements Serializable{

    private int current;

    private List<MasterNewsDto> masterNewsDtoList;

    private boolean hasMore;

    private long count;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<MasterNewsDto> getMasterNewsDtoList() {
        return masterNewsDtoList;
    }

    public void setMasterNewsDtoList(List<MasterNewsDto> masterNewsDtoList) {
        this.masterNewsDtoList = masterNewsDtoList;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
