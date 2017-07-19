package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIPlayUrlRequest;

import java.util.Map;

/**
 * Created by nick on 2017/7/19.
 * 获取视频播放地址接口
 */
public class VodGetPlayInfoHandler extends VodOperatorAdapter implements VodHandler {

    public VodGetPlayInfoHandler(RequestType requestType, HttpRequestExecutorManager manager, VodAPIRequestBuilder builder){
        this.requestType = requestType;
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void handle() {
        call();
    }

    @Override
    public void action(Action action) {
        setAction(action);
    }

    public void setAction(Action action){
        vodAPIRequest.setAction(action.name());
        selfParam.put("Action", action.name());
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setVodId((String)selfParam.get("videoId"));
    }

    private void setVodId(String vodId){
        ((VodAPIPlayUrlRequest)vodAPIRequest).setVideoId(vodId);
    }

    @Override
    public void getPlayUrl(){
        //do nothing
    }

    @Override
    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }
}
