package com.fct.web.pay.http.filters.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ningyang
 */
public abstract class AbstractHeaderInterceptor implements HandlerInterceptor {

    public static class HeaderCode {
        public String headerKey;
        int errorCode;

        HeaderCode(String headerKey, int errorCode) {
            this.headerKey = headerKey;
            this.errorCode = errorCode;
        }
    }

    protected Boolean isSandbox = false;

    public static final HeaderCode[] headers = new HeaderCode[]{
            new HeaderCode("app-version", 8),
            new HeaderCode("channel", 3),
            new HeaderCode("version", 1)
    };

    public static final HeaderCode[] otherHeaders = new HeaderCode[]{
            new HeaderCode("request-id", 14),
            new HeaderCode("client-request-time", 15),
            new HeaderCode("signature", 20)
    };

    protected Boolean skipHeaderCheck(HttpServletRequest request) {
        return isSandbox && "true".equals(request.getHeader("header-check-skip"));
    }
}
