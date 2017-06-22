package com.fct.thirdparty.http.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by ningyang on 2017/5/13.
 */
public class QueryMapUtils {

    /**
     * 转换查询条件为string数组
     * @param queryMap
     * @return
     */
    public static String[] convert(Map<String, Object> queryMap) {
        String[] result = new String[queryMap.size() * 2];
        int i = 0;
        for(Iterator<String> it = queryMap.keySet().iterator(); it.hasNext(); i += 2) {
            String key = it.next();
            result[i] = key;
            result[i + 1] = queryMap.get(key).toString();
        }

        return result;
    }
}
