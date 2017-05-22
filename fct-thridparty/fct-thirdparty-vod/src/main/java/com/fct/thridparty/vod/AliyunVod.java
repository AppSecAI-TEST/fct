package com.fct.thridparty.vod;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.factory.VodHandlerFactory;
import com.fct.thridparty.vod.handler.VodHandler;
import com.fct.thridparty.vod.response.VodResponse;
import com.fct.thridparty.vod.utils.SignatureGenerator;

import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
public class AliyunVod {

    private VodHandler handler;
    private HttpRequestExecutorManager manager;
    private RequestType requestType;
    private String accessKeyId;
    private String accessKeySecret;

    public AliyunVod(HttpRequestExecutorManager manager, RequestType requestType,
                     String accessKeyId, String accessKeySecret){
        this.manager = manager;
        this.requestType = requestType;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        init();
    }

    protected void init(){
        handler = VodHandlerFactory.getHandler(requestType, manager);
        handler.commonParam(accessKeyId);
        handler.setAccessKeySecret(accessKeySecret);
    }

    public AliyunVod withSelfParam(Map<String, Object> selfParam){
        handler.selfParam(selfParam);
        return this;
    }

    public AliyunVod signature(){
        handler.allParam();
        handler.signature(SignatureGenerator.generator(handler, accessKeySecret));
        return this;
    }

    public AliyunVod buildRequest(){
        handler.buildRequest();
        return this;
    }

    public AliyunVod run(){
        handler.handle();
        return this;
    }

    public VodResponse response(){
        return handler.getResponse();
    }

    public AliyunVod action(Action action){
        handler.action(action);
        return this;
    }
}
