package com.fct.thridparty.vod.handler;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIUploadRequest;
import com.fct.thridparty.vod.response.VodUploadResponse;

import java.io.File;
import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
public class VodUploadHandler extends VodOperatorAdapter implements VodHandler{

    public VodUploadHandler(RequestType requestType, VodAPIRequestBuilder builder){
        this.builder = builder;
        this.requestType = requestType;
    }

    @Override
    public void upload() {
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse uploadVideoResponse = uploader.uploadVideo(((VodAPIUploadRequest) vodAPIRequest).getUploadVideoRequest());
        response = new VodUploadResponse(uploadVideoResponse);
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setTitle((String) selfParam.get("title"));
        setFile((File) selfParam.get("file"));
        setTags((String) selfParam.get("tags"));
        setCoverUrl((String) selfParam.get("coverUrl"));
        setDescription((String) selfParam.get("description"));
        setCateId((Integer) selfParam.get("cateId"));
        setCallBackUrl((String) selfParam.get("callBackUrl"));
        ((VodAPIUploadRequest) vodAPIRequest).setAccessKeySecret(accessKeySecret);
        ((VodAPIUploadRequest) vodAPIRequest).initUploadVideoRequest();
    }

    /**
     *
     *    private String accessKeySecret;
     *    private File file;
     *    private String title;
     *    private UploadVideoRequest uploadVideoRequest;
     *    private String tags;
     *    private String coverUrl;
     *    private String description;
     *    private String callbackUrl;
     *    private String cateId;
     * @param title
     */
    private void setTitle(String title){
        ((VodAPIUploadRequest) vodAPIRequest).setTitle(title);
    }

    private void setFile(File file){
        ((VodAPIUploadRequest) vodAPIRequest).setFile(file);
    }

    private void setTags(String tags){
        ((VodAPIUploadRequest) vodAPIRequest).setTags(tags);
    }

    private void setCoverUrl(String coverUrl){
        ((VodAPIUploadRequest) vodAPIRequest).setCoverUrl(coverUrl);
    }

    private void setDescription(String description){
        ((VodAPIUploadRequest) vodAPIRequest).setDescription(description);
    }

    private void setCallBackUrl(String callBackUrl){
        ((VodAPIUploadRequest) vodAPIRequest).setCallbackUrl(callBackUrl);
    }

    private void setCateId(int cateId){
        ((VodAPIUploadRequest) vodAPIRequest).setCateId(cateId);
    }

    @Override
    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }

    @Override
    public void handle() {
        upload();
    }

    @Override
    public void action(Action action) {
        setAction(action);
    }
}
