package com.fct.thridparty.vod.handler;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.operator.VodOperatorAdapter;
import com.fct.thridparty.vod.request.VodAPIGetVideosRequest;

import java.util.Map;

/**
 * Created by ningyang on 2017/5/22.
 */
public class VodGetVideosHandler extends VodOperatorAdapter implements VodHandler{

    public VodGetVideosHandler(RequestType requestType, HttpRequestExecutorManager manager, VodAPIRequestBuilder builder){
        this.requestType = requestType;
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void getVods(){
        call();
    }

    @Override
    public void handle() {
        getVods();
    }

    @Override
    public void action(Action action) {
        setAction(action);
    }

    @Override
    public void selfParam(Map<String, Object> selfParam) {
        this.selfParam = selfParam;
        setSortBy((String) selfParam.get("SortBy"));
        setStatus((String) selfParam.get("Status"));
        setCateId((Integer) selfParam.get("CateId"));
        setPageSize((Integer) selfParam.get("PageSize"));
        setPageNo((Integer) selfParam.get("PageNo"));
    }

    /**
     * private String Status;

     //视频分类ID
     private int CateId;

     //页号，默认1
     private String PageNo;

     //可选，默认10，最大不超过100
     private String PageSize;

     //结果排序，范围：CreateTime:Desc、CreateTime:Asc，默认为CreateTime:Desc
     private String SortBy;
     * @param status
     */
    private void setStatus(String status){
        ((VodAPIGetVideosRequest) vodAPIRequest).setStatus(status);
    }

    private void setCateId(int cateId){
        ((VodAPIGetVideosRequest) vodAPIRequest).setCateId(cateId);
    }

    private void setPageSize(int pageSize){
        ((VodAPIGetVideosRequest) vodAPIRequest).setPageSize(pageSize);
    }

    private void setSortBy(String sortBy){
        ((VodAPIGetVideosRequest) vodAPIRequest).setSortBy(sortBy);
    }

    private void setPageNo(int pageNo){
        ((VodAPIGetVideosRequest) vodAPIRequest).setPageNo(pageNo);
    }

    /**
     *
     * @param signature
     * @return
     */
    @Override
    public VodHandler signature(String signature) {
        setSignature(signature);
        return this;
    }
}
