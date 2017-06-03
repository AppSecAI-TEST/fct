package com.fct.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jon on 2017/5/31.
 */
public class PageUtil {


    public static String getPageSQL(String tableName,String fields, String condition, String orderBy, Integer startIndex,
    Integer count)
    {
        if (StringUtils.isEmpty(fields))
        {
            fields = "*";
        }
        if (StringUtils.isEmpty(orderBy))
        {
            throw new IllegalArgumentException("orderBy");
        }
        if (startIndex < 1)
        {
            startIndex = 1;
        }
        if (count < 1)
        {
            throw new IllegalArgumentException("所取数据条数不能小于1");
        }
        if (count > 1000)
        {
            throw new IllegalArgumentException("不能一次取超过 1000 条的数据");
        }

        String query;
        query = String.format("select %s from %s %s order by %s limit %d,%d;",

                fields, tableName,StringUtils.isEmpty(condition) ? "" : " where 1=1 " + condition,orderBy, (startIndex-1)*count,count);

        return query;
    }
}
