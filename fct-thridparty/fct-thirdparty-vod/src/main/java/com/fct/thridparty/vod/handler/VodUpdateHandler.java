package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIUpdateRequest;

import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 * 修改视频信息
 */
public class VodUpdateHandler extends VodOperatorAdapter implements VodHandler{

    public VodUpdateHandler(RequestType requestType, HttpRequestExecutorManager manager, VodAPIRequestBuilder builder){
        this.builder = builder;
        this.manager = manager;
        this.requestType = requestType;
    }

    /**
     * private String VideoId;
     * private String Title;
     * private String Description;
     * private String CoverURL;
     * private String CateId;
     * private String[] Tags;
     */
    private void setVideoId(String VideoId){
        ((VodAPIUpdateRequest)vodAPIRequest).setVideoId(VideoId);
    }

    private void setTitle(String Title){
        ((VodAPIUpdateRequest)vodAPIRequest).setTitle(Title);
    }

    private void setDescription(String Description){
        ((VodAPIUpdateRequest)vodAPIRequest).setDescription(Description);
    }

    private void setCoverURL(String CoverURL){
        ((VodAPIUpdateRequest)vodAPIRequest).setCoverURL(CoverURL);
    }

    private void setCateId(String CateId){
        ((VodAPIUpdateRequest)vodAPIRequest).setCateId(CateId);
    }

    private void setTags(String tags){
        ((VodAPIUpdateRequest)vodAPIRequest).setTags(tags);
    }

    @Override
    public void updateVod() {
        call();
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setVideoId((String) this.selfParam.get("videoId"));
        setTitle((String) this.selfParam.get("title"));
        setDescription((String) this.selfParam.get("description"));
        setCoverURL((String) this.selfParam.get("coverUrl"));
        setCateId((String) this.selfParam.get("catId"));
        setTags((String)this.selfParam.get("tags"));
    }

    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void handle() {
        updateVod();
    }

    @Override
    public void action(String action) {
        setAction(action);
    }
}
