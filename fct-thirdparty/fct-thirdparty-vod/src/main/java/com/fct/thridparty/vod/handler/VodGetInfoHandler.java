package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIGetInfoRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Map;


/**
 * Created by nick on 2017/5/19.
 * 这个handler类既可以当获取单个视频信息的功能,
 * 还包含获取播放凭证的功能
 *
 */
public class VodGetInfoHandler extends VodOperatorAdapter implements VodHandler{

    public VodGetInfoHandler(RequestType requestType, HttpRequestExecutorManager manager, VodAPIRequestBuilder builder){
        this.manager = manager;
        this.builder = builder;
        this.requestType = requestType;
    }

    private void setVodId(String vodId){
        ((VodAPIGetInfoRequest)vodAPIRequest).setVideoId(vodId);
    }

    @Override
    public void getVod() {
        if(vodAPIRequest==null && StringUtils.isEmpty(((VodAPIGetInfoRequest)vodAPIRequest).getVideoId()))
            throw new IllegalArgumentException("get video request should not be null and video id should not empty");
        call();
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setVodId((String) this.selfParam.get("VodId"));
    }

    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void handle() {
        getVod();
    }

    @Override
    public void action(Action action) {
        setAction(action);
    }
}
