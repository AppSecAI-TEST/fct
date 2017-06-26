package com.fct.web.pay.http.filters;


import com.fct.web.pay.utils.servlet.MultiReadHttpServletRequest;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author ningyang
 */
public class RequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MultiReadHttpServletRequest wrapped = new MultiReadHttpServletRequest(servletRequest);
        filterChain.doFilter(wrapped, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
