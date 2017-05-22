package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIDeleteRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
public class VodDeleteHandler extends VodOperatorAdapter implements VodHandler{

    public VodDeleteHandler(RequestType requestType, HttpRequestExecutorManager manager, VodAPIRequestBuilder builder){
        this.manager = manager;
        this.builder = builder;
        this.requestType = requestType;
    }

    private void setVideoIds(String VideoIds){
        ((VodAPIDeleteRequest)vodAPIRequest).setVideoIds(VideoIds);
    }

    @Override
    public void deleteVod() {
        if(vodAPIRequest==null&& StringUtils.isEmpty(((VodAPIDeleteRequest)vodAPIRequest).getVideoIds()))
            throw new IllegalArgumentException("delete video request should not be null and video ids should not empty");
        call();
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setVideoIds((String) this.selfParam.get("videoIds"));
    }

    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void handle() {
        deleteVod();
    }

    @Override
    public void action(String action) {
        setAction(action);
    }
}
