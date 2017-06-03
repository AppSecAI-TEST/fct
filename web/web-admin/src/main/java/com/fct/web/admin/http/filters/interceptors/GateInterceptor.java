package com.fct.web.admin.http.filters.interceptors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fct.web.admin.annotations.IgnoreGateLog;
import com.fct.web.admin.annotations.ParamIgnore;
import com.fct.web.admin.http.support.version.APIScanner;
import com.fct.web.admin.session.Session;
import com.fct.web.admin.utils.SessionUtil;
import com.fct.web.admin.utils.servlet.ServletAttributeCacheUtil;
import com.fct.web.admin.utils.servlet.ServletRequestIPAddressUtil;
import com.fct.common.json.JsonConverter;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ningyang
 */
public final class GateInterceptor extends AbstractHeaderInterceptor {
    private static final Logger logger = LoggerFactory.getLogger("GATE");
    private static final String requestStartTimeAttributeKey = "request_start";

    private static final ImmutableSet<String> logIgnore;
    private static final ImmutableSet<String> paramIgnore;
    private SessionUtil sessionUtil;

    public GateInterceptor(SessionUtil sessionUtil){
        this.sessionUtil = sessionUtil;
    }
    static {
        List<String> results = APIScanner.getAPIsByExistAnnotation(IgnoreGateLog.class);
        List<String> paramsIgnores = APIScanner.getParamIgnoreAnnot(ParamIgnore.class);
        logIgnore = ImmutableSet.copyOf(results);
        paramIgnore = ImmutableSet.copyOf(paramsIgnores);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long start = System.currentTimeMillis();
        request.setAttribute(requestStartTimeAttributeKey, start);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long end = System.currentTimeMillis();
        Long start = (Long) request.getAttribute(requestStartTimeAttributeKey);

        String method = request.getMethod();
        String URI = request.getServletPath();

        String remoteIp = ServletRequestIPAddressUtil.parse(request);

        Long duration = end - start;


        LogBuilder node = LogBuilder.builder();

        node.put("project_id", "02");
        node.put("action_time", System.currentTimeMillis());
        node.put("url", method + URI);
        node.put("ip", remoteIp);
        node.put("duration", duration);
        node.put("status", response.getStatus());
        node.put("request-id", request.getHeader("request-id"));
        node.put("headers", ServletAttributeCacheUtil.getHeaderStr(request));
        if (!logIgnore.contains(method + URI)) {
            node.put("query", request.getQueryString());
            String body = Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8);
            if (StringUtils.isNotBlank(body)) {
                node.put("body", JsonConverter.toJsonNode(body));
            }
        }

        Session session = ServletAttributeCacheUtil.getSession(request, sessionUtil);
        if (session != null) {
            node.put("uid", session.getUserId());
        }else {
            node.put("uid", "");
        }

        logger.info(node.build());
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private static class LogBuilder {

        private static final JsonNodeFactory factory = JsonNodeFactory.instance;
        private ObjectNode node;

        LogBuilder() {
            node = factory.objectNode();
        }

        static LogBuilder builder() {
            return new LogBuilder();
        }

        String build() {
            return node.toString();
        }

        LogBuilder put(String key, Object value) {
            if (value == null || StringUtils.isBlank(value.toString())) {
                return this;
            }
            if (value instanceof JsonNode) {
                node.set(key, (JsonNode) value);
            } else {
                if (StringUtils.isNotBlank(value.toString())) {
                    node.put(key, value.toString());
                }
            }
            return this;
        }
    }
}
