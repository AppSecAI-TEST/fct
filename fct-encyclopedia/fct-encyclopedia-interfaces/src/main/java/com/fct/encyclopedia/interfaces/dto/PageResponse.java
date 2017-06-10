package com.fct.encyclopedia.interfaces.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nick on 2017/5/31.
 */
public class PageResponse<T> implements Serializable{

    private List<T> elements;
    private int current;
    private boolean hasMore;
    private long totalCount;

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
