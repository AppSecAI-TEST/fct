package com.fct.api.http.http.exceptions.handlers;

import com.fct.api.http.http.exceptions.ErrorMessage;
import com.fct.api.http.utils.JsonModelAndViewBuilder;
import com.fct.api.http.utils.servlet.ServletAttributeCacheUtil;
import com.fct.api.http.utils.servlet.ServletRequestIPAddressUtil;
import com.fct.common.exceptions.Exceptions;
import com.google.common.base.Charsets;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ningyang
 */
public final class DefaultExceptionHandler implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger("EX");

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            response.setStatus(400);
            MissingServletRequestParameterException msrpe = (MissingServletRequestParameterException) ex;
            return JsonModelAndViewBuilder.build(new ErrorMessage(String.format("[%s]字段不能为空", msrpe.getParameterName())));
        } else {
            response.setStatus(500);
            logger.info(request.getHeader("request-id") + " " + request.getMethod() + request.getServletPath());
            logger.info(request.getQueryString());
            try {
                logger.info(Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8));
            } catch (IOException e) {
                //ignore
            }
            logger.info(getHeaderParam(request));
            logger.info(Exceptions.getStackTraceAsString(ex));
            return JsonModelAndViewBuilder.build(new ErrorMessage("内部错误"));
        }
    }

    private String getHeaderParam(HttpServletRequest request){

        String method = request.getMethod();
        String URI = request.getServletPath();
        String remoteIp = ServletRequestIPAddressUtil.parse(request);
        StringBuilder sb = new StringBuilder(512);
        sb.append(" ");
        sb.append(method);
        sb.append(URI);
        sb.append(" ");
        sb.append(remoteIp);
        sb.append(" ");
        sb.append(ServletAttributeCacheUtil.getHeaderStr(request));
        return sb.toString();
    }
}
