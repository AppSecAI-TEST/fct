package com.fct.api.http.http.filters.interceptors;

import com.fct.api.http.annotations.WithoutToken;
import com.fct.api.http.http.dto.json.JsonResponseEntity;
import com.fct.api.http.http.support.version.APIScanner;
import com.fct.api.http.session.Session;
import com.fct.api.http.utils.ErrorMessageSelector;
import com.fct.api.http.utils.SessionUtil;
import com.fct.common.json.JsonConverter;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author ningyang
 */
public final class RequestAccessTokenInterceptor extends AbstractHeaderInterceptor {

    private static final String accesstokenHeader = "access-token";

    private static final ImmutableSet<String> exclude;

    private SessionUtil sessionUtil;

    static {
        List<String> parseFromPackage = APIScanner.getAPIsByExistAnnotation(WithoutToken.class);
        exclude = ImmutableSet.copyOf(parseFromPackage);
    }

    public RequestAccessTokenInterceptor(SessionUtil sessionUtil, Boolean isSandbox) {
        this.isSandbox = isSandbox;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (skipHeaderCheck(request)) {
            return true;
        }

        String method = request.getMethod();
        String URI = request.getServletPath();

        String token = request.getHeader(accesstokenHeader);
        if (StringUtils.isBlank(token)) {
            if (!ifExclude(method, URI)) {
                return buildGuestResponseBody(response, 10, ErrorMessageSelector.getOne());
            }
        } else {//token!=null
            Session session = sessionUtil.get(token);
            if (session == null) {
                return buildGuestResponseBody(response, 12, "登录凭证过期, 请重新登录");
            }
//            else if (!session.getIsValid()) {
//                return buildGuestResponseBody(response, 13, "账户在其他设备登录, 请重新登录");
//            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private boolean buildGuestResponseBody(HttpServletResponse response, int code, String msg) {//return a guest token when token check failed.
        try {
            PrintWriter writer = response.getWriter();
            response.setContentType("application/json;charset=UTF-8");
            JsonResponseEntity result = new JsonResponseEntity();
            result.setMsg(msg);
            result.setCode(code);
            writer.write(JsonConverter.toJson(result));
            writer.close();
        } catch (IOException e) {
            //ignore
        }
        return false;
    }

    private boolean ifExclude(String method, String URI) {
        return exclude.contains(method + URI);
    }
}