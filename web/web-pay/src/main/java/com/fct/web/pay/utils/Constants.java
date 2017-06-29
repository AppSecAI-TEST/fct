package com.fct.web.pay.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public Constants()
    {

    }

    public static final Logger logger = LoggerFactory.getLogger("EX");

    public static final String domain = "http://www.fangcuntang.com";

    public static final String payDomain = "http://pay.fangcuntang.com";

    public static Map<String, String> getRequestData(HttpServletRequest request)
    {
        Map<String,String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();


        Map<String, String> reqParam = getAllRequestParam(req);

        Map<String, String> valideData = null;

        if (null != reqParam && !reqParam.isEmpty()) {

            Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();

            valideData = new HashMap<String, String>(reqParam.size());

            while (it.hasNext()) {

                Entry<String, String> e = it.next();

                String key = (String) e.getKey();

                String value = (String) e.getValue();

                value = new String(value.getBytes(encoding), encoding);

                valideData.put(key,value);

            }

        }


        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        return params;
    }
}
