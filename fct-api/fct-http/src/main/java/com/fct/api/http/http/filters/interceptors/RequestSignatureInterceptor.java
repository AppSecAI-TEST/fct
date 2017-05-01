package com.fct.api.http.http.filters.interceptors;

import com.fct.api.http.session.Session;
import com.fct.api.http.utils.SessionUtil;
import com.fct.api.http.utils.security.HMAC;
import com.fct.api.http.utils.servlet.ServletAttributeCacheUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

/**
 * @author ningyang
 */
public final class RequestSignatureInterceptor extends AbstractHeaderInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestSignatureInterceptor.class);

    private SessionUtil sessionUtil;

    public RequestSignatureInterceptor(SessionUtil sessionUtil, Boolean isSandbox) {
        this.sessionUtil = sessionUtil;
        this.isSandbox = isSandbox;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        String signatureFromClient = request.getHeader("signature");
        String method = request.getMethod().toUpperCase();
        String path = request.getServletPath().toLowerCase();

        String requestId = request.getHeader("request-id");
        String timestamp = request.getHeader("client-request-time");
        String headerStr = ServletAttributeCacheUtil.getHeaderStr(request);
        Session session = ServletAttributeCacheUtil.getSession(request, sessionUtil);
        String secret = session == null ? "1200a3809c56463f92ab0a93f362325e" : session.getSecret();
        String prefix = "idon'tcarewhoyouarejustgodie!\n";
        String appSecret = "a9b5d0fb-409d-46d9-9c11-339fdc21e4ca";
        String parameterStr;
        if ("GET".equals(method) || "DELETE".equals(method)) {
            parameterStr = getMethodParameterStr(request);
        } else {
            parameterStr = postMethodParameterStr(request);
        }
        String signatureStr = prefix +
                "request-id=" + requestId + "@client-request-time=" + timestamp + "\n"
                + path + "+access-token=" + StringUtils.defaultString(request.getHeader("access-token")) + "&" + headerStr + "+" + parameterStr + "\n"
                + appSecret;
        String calculated = HMAC.calculateRFC2104HMAC(signatureStr, secret);
        if (calculated.equals(signatureFromClient)) {
            return true;
        } else {
            logger.info("error signature generate from " + signatureStr);
            buildResponse(response, 21, "签名错误");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String postMethodParameterStr(HttpServletRequest request) throws Exception {
        return Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8);
    }

    private String getMethodParameterStr(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        List<String> queryList = new ArrayList<>();
        List<String> sortedParameterNameList = sortParameterNames(parameterNames);
        for (String name : sortedParameterNameList) {
            queryList.add(String.format("%s=%s", name, request.getParameter(name)));
        }
        return StringUtils.join(queryList, "&");
    }

    private List<String> sortParameterNames(Enumeration<String> parameterNames) {
        List<String> namesList = Lists.newLinkedList();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            ListIterator<String> iterator = namesList.listIterator();
            Boolean inserted = false;
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (parameterName.compareTo(s) < 0) {
                    iterator.previous();
                    iterator.add(parameterName);
                    inserted = true;
                    break;
                }
            }
            if (inserted) {
                continue;
            }
            namesList.add(parameterName);
        }
        return namesList;
    }

    private void buildResponse(HttpServletResponse response, int code, String msg) {
        try {
            PrintWriter writer = response.getWriter();
            response.setContentType("application/json;charset=UTF-8");
            writer.write(String.format("{\"code\":%d,\"msg\":\"%s\"}", code, msg));
            writer.close();
        } catch (IOException e) {
            //ignore
        }
    }
}
