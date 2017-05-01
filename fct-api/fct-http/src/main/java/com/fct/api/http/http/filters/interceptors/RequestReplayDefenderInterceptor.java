package com.fct.api.http.http.filters.interceptors;

import com.fct.api.http.utils.security.ReplayAttackDefender;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ningyang
 */
public class RequestReplayDefenderInterceptor extends AbstractHeaderInterceptor {

    private ReplayAttackDefender defender;

    public RequestReplayDefenderInterceptor(ReplayAttackDefender defender, Boolean isSandbox) {
        this.isSandbox = isSandbox;
        this.defender = defender;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        String requestId = request.getHeader("request-id");
        if (!defender.check(requestId)) {
            try {
                PrintWriter writer = response.getWriter();
                response.setContentType("application/json;charset=UTF-8");
                writer.write("{\"code\":17,\"msg\":\"网络拥堵,请稍后再试\"}"); //重放攻击
                writer.close();
            } catch (IOException e) {
                //ignore
            }
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
