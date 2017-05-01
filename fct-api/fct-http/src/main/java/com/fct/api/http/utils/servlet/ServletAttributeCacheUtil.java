package com.fct.api.http.utils.servlet;


import com.fct.api.http.http.filters.interceptors.AbstractHeaderInterceptor;
import com.fct.api.http.session.Session;
import com.fct.api.http.utils.SessionUtil;
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

    public static Session getSession(HttpServletRequest request, SessionUtil sessionUtil) {
        Session session = (Session) request.getAttribute("session");
        if (session == null) {
            String accessToken = request.getHeader("access-token");
            if (StringUtils.isNotBlank(accessToken)) {
                session = sessionUtil.get(accessToken);
                request.setAttribute("session", session);
            }
        }
        return session;
    }
}
