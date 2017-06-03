package com.fct.web.admin.http.support.session;

import com.fct.web.admin.http.exceptions.ErrorMessage;
import com.fct.web.admin.utils.JsonModelAndViewBuilder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ningyang
 */
public class SessionExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof AccessTokenMissingException) {
            return JsonModelAndViewBuilder.build(new ErrorMessage((AccessTokenMissingException) ex));
        } else if (ex instanceof LoginRequiredException) {
            return JsonModelAndViewBuilder.build(new ErrorMessage((LoginRequiredException) ex));
        } else {
            return null;
        }
    }
}