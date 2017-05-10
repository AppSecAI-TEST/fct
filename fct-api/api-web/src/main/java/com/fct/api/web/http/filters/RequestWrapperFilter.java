package com.fct.api.web.http.filters;




import com.fct.api.web.utils.servlet.MultiReadHttpServletRequest;

import java.io.IOException;
import javax.servlet.*;

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
