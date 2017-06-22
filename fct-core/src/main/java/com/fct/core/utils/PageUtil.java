package com.fct.core.utils;

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

    /// <summary>
    /// 生成分页的HTML代码
    /// </summary>
    /// <param name="nTotal">总条数</param>
    /// <param name="nCurrentPage">当前页</param>
    /// <param name="nPageSize">每页数</param>
    /// <param name="sUrlBase">地址</param>
    /// <returns></returns>
    public static String getPager(Integer total, Integer currentPage, Integer pageSize, String urlBase)
    {
        if (total <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        if (currentPage > 1)
        {
            sb.append("<a href=\"" + String.format(urlBase, currentPage - 1) + "\" class=\"page_able\">«上一页</a>");
        }
        else
        {
            sb.append("<span class=\"page_off\">«</span>");
        }
        int nPages = 0;
        if (total % pageSize > 0)
            nPages = (total / pageSize) + 1;
        else
            nPages = (total / pageSize);

        if (currentPage > nPages)
            currentPage = nPages;
        if (currentPage < 1)
            currentPage = 1;

        if (currentPage > 5)
        {
            sb.append(loopStr(1, 2, currentPage, urlBase));
            sb.append("<span class=\"page_on\">...</span>");
            if (nPages > currentPage + 3)
            {
                sb.append(loopStr(currentPage - 2, currentPage + 2, currentPage, urlBase));
                sb.append("<span class=\"page_on\">...</span>");
                sb.append(loopStr(nPages - 1, nPages, currentPage, urlBase));
            }
            else
            {
                sb.append(loopStr(currentPage - 2, nPages, currentPage, urlBase));
            }
        }
        else
        {
            if (nPages < currentPage + 5)
            {
                sb.append(loopStr(1, nPages, currentPage, urlBase));
            }
            else
            {
                sb.append(loopStr(1, currentPage + 2, currentPage, urlBase));
                sb.append("<span class=\"page_on\">...</span>");
                sb.append(loopStr(nPages - 1, nPages, currentPage, urlBase));
            }
        }

        if (currentPage < nPages)
        {
            sb.append(" <a href=\"" + String.format(urlBase, currentPage + 1) + "\" class=\"page_able\">下一页»</a>");
        }
        else
        {
            sb.append("<span class=\"page_off\">»</span>");
        }
        return sb.toString();
    }

    //生成HTML代码用
    private static StringBuilder loopStr(Integer min, Integer max, Integer currentPage, String sUrlBase)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = min; i <= max; i++)
        {
            if (i == currentPage)
                sb.append(String.format("<strong class=\"page_on\">%d</strong>", i));
            else
                sb.append(String.format("<a href=\"%s\" class=\"page_able\">%d</a>", String.format(sUrlBase, i), i));
        }
        return sb;
    }
}