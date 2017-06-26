package com.fct.web.pay.utils.servlet;


import com.fct.web.pay.http.filters.interceptors.AbstractHeaderInterceptor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ningyang
 */
public class ServletAttributeCacheUtil {

    public static String getHeaderStr(HttpServletRequest request) {
        String headerStr = (String) request.getAttribute("headerStr");
        if (headerStr == null) {
            StringBuilder headerBuilder = new StringBuilder(256);
            for (int i = 0; i < AbstractHeaderInterceptor.headers.length; i++) {
                if (i != 0) {
                    headerBuilder.append("&");
                }
                headerBuilder.append(AbstractHeaderInterceptor.headers[i].headerKey);
                headerBuilder.append("=");
                headerBuilder.append(StringUtils.defaultString(request.getHeader(AbstractHeaderInterceptor.headers[i].headerKey)));
            }
            headerStr = headerBuilder.toString();
            request.setAttribute("headerStr", headerStr);
        }
        return headerStr;
    }
}
