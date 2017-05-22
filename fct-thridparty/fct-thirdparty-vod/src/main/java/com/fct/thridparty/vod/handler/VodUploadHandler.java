package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;

import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
public class VodUploadHandler extends VodOperatorAdapter implements VodHandler{

    public VodUploadHandler(RequestType requestType, HttpRequestExecutorManager manager,
                            VodAPIRequestBuilder builder){
        this.manager = manager;
        this.builder = builder;
        this.requestType = requestType;
    }

    @Override
    public void upload() {
        //do nothing
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
    }

    @Override
    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }

    @Override
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    @Override
    public void handle() {
        upload();
    }

    @Override
    public void action(String action) {
        setAction(action);
    }
}
