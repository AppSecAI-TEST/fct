package com.fct.api.web.http.support.session;


import com.fct.api.web.session.Session;
import com.fct.api.web.utils.SessionUtil;
import com.fct.api.web.utils.servlet.ServletAttributeCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ningyang
 */
public class AccessTokenResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SessionUtil sessionService;

    public AccessTokenResolver(SessionUtil sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return parameter.hasParameterAnnotation(AccessToken.class) || Session.class == paramType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Session session = ServletAttributeCacheUtil.getSession(servletRequest, sessionService);

        AccessToken annotation = parameter.getParameterAnnotation(AccessToken.class);
        if (session == null) {
            throw new AccessTokenMissingException();
        } else if (session.isGuest() && !annotation.guestEnabled()) {
            throw new LoginRequiredException();
        } else {
            return session;
        }
    }
}
