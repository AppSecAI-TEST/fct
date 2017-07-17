package com.fct.web.pay.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public Constants()
    {

    }

    public static final Logger logger = LoggerFactory.getLogger("EX");

    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request,Boolean urlDecode) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        try {
            if (null != temp) {
                while (temp.hasMoreElements()) {
                    String key = (String) temp.nextElement();
                    String value = request.getParameter(key);

                    //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                    //value = new String(value.getBytes("ISO-8859-1"), "utf-8");
                    if (urlDecode) {
                        value = new String(value.getBytes("utf-8"), "utf-8");
                    }
                    res.put(key, value);
                    //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                    //System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
                    if (null == res.get(key) || "".equals(res.get(key))) {
                        res.remove(key);
                    }
                }
            }
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
        }
        return res;
    }

    public static Map<String, String> getRequestData(String readContent,Boolean urlDecode)
    {
        Map<String, String> sArray = new HashMap<>();

        try {
            //对url第一个字符？过滤
            //string query = readContent.Replace("?", "");
            if (!StringUtils.isEmpty(readContent)) {
                //根据&符号分隔成数组
                String[] coll = readContent.split("&");
                //定义临时数组
                String[] temp = {};
                //循环各数组
                for (int i = 0; i < coll.length; i++) {
                    //根据=号拆分
                    temp = coll[i].split("=");
                    //把参数名和值分别添加至SortedDictionary数组
                    String value = temp[1];
                    if (urlDecode) {
                        value = new String(value.getBytes("utf-8"), "utf-8");
                        value = URLDecoder.decode(value,"utf-8");
                    }
                    sArray.put(temp[0], value);
                }
            }
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
        }
        return sArray;
    }
}
