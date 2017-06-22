package com.fct.core.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by jon on 2017/5/12.
 */
public class QueryResult<T> {

    private int totalCount;

    private List<T> items;

    private Map<String,String> extand;

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> itmes)
    {
        this.items = items;
    }

    public Map<String,String> getExtand() {
        return this.extand;
    }

    public void setExtand(Map<String,String> extand)
    {
        this.extand = extand;
    }

}
