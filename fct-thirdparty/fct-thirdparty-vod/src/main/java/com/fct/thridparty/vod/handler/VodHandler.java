package com.fct.thridparty.vod.handler;

import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.callback.Callback;
import com.fct.thridparty.vod.request.VodAPIRequest;
import com.fct.thridparty.vod.response.VodResponse;

import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
public interface VodHandler {

    void setAccessKeySecret(String accessKeySecret);

    VodResponse getResponse();

    void handle();

    void action(Action action);

    void commonParam(String accessKeyId);

    void selfParam(Map<String, Object> selfParam);

    void allParam();

    VodHandler signature(String signature);

    void buildRequest();

    VodAPIRequest getRequest();

    Map<String, Object> getAllParam();

    void setCallback(Callback callBack);
}
