package com.fct.web.admin.http.exceptions.handlers;

import com.fct.web.admin.http.exceptions.ErrorMessage;
import com.fct.web.admin.utils.JsonModelAndViewBuilder;
import com.fct.common.exceptions.BaseException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ningyang
 */
public class ServiceExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof BaseException) {
            response.setStatus(200);
            return JsonModelAndViewBuilder.build(new ErrorMessage((BaseException) ex));
        }

        return null;
    }
}
