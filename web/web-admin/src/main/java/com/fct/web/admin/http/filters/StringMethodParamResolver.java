package com.fct.web.admin.http.filters;

import com.fct.web.admin.annotations.NoNullValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nick on 2017/6/5.
 */
public class StringMethodParamResolver implements HandlerMethodArgumentResolver {


    /**
     * 只要加了NoNullValue注解的方法就会去判断方法参数是不是null如果是null就返回""
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> paramType = methodParameter.getParameterType();
        return methodParameter.hasParameterAnnotation(NoNullValue.class) || String.class == paramType;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest servletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String paramName = methodParameter.getParameterName();
        String paramValue = servletRequest.getParameter(paramName);
        if(StringUtils.isEmpty(paramValue)){
            paramValue = "";
        }
        return paramValue;
    }
}
