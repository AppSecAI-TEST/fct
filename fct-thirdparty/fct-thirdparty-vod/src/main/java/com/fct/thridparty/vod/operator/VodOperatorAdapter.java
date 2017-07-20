package com.fct.thridparty.vod.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fct.core.utils.UUIDUtil;
import com.fct.thirdparty.http.builder.RequestBuilder;
import com.fct.thirdparty.http.entity.JsonNodeResponseWrapper;
import com.fct.thirdparty.http.util.JsonConverter;
import com.fct.thridparty.vod.Action;
import com.fct.thridparty.vod.request.VodAPIPlayUrlRequest;
import com.fct.thridparty.vod.response.*;
import com.fct.thridparty.vod.utils.SignatureGenerator;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Request;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 */
@Data
public abstract class VodOperatorAdapter extends AbstractVodOperator {

    private String Format;
    private String Version;
    private String AccessKeyId;
    private String SignatureMethod;
    private String Timestamp;
    private String SignatureVersion;
    private String SignatureNonce;
    private static final String FORMAT_ENCODE = "UTF-8";

    @Override
    public void upload() {
        //do nothing
    }

    @Override
    public void deleteVod() {
        //do nothing
    }

    @Override
    public void getVod() {
        //do nothing
    }

    @Override
    public void getVods() {
        //do nothing
    }

    @Override
    public void updateVod() {
        //do nothing
    }

    @Override
    public void getPlayUrl(){
        //do nothing
    }

    public void setAction(Action action){
        vodAPIRequest.setAction(action.name());
        selfParam.put("Action", action.name());
    }

    public void commonParam(String accessKeyId){
        initCommonParam(accessKeyId);
    }

    private void initCommonParam(String accessKeyId){
        setFormat("JSON");
        setVersion("2017-03-21");
        setAccessKeyId(accessKeyId);
        setSignatureMethod("HMAC-SHA1");
        setSignatureNonce(UUIDUtil.generateUUID());
        setTimestamp(ISO8601TimeFormate(new Date()));
        setSignatureVersion("1.0");
        commonParam = Maps.newHashMap();
        commonParam.putIfAbsent("Format", Format);
        commonParam.putIfAbsent("Version", Version);
        commonParam.putIfAbsent("AccessKeyId", AccessKeyId);
        commonParam.putIfAbsent("SignatureMethod", SignatureMethod);
        commonParam.putIfAbsent("SignatureNonce", SignatureNonce);
        commonParam.putIfAbsent("Timestamp", Timestamp);
        commonParam.putIfAbsent("SignatureVersion", SignatureVersion);
    }

    @Override
    public void buildRequest() {
        vodAPIRequest = builder.accessKeyId(AccessKeyId).
                                format(Format).
                                timestamp(Timestamp).
                                signatureMethod(SignatureMethod).
                                signatureNonce(SignatureNonce).
                                signatureVersion(SignatureVersion).
                                build();
    }

    public VodResponse getResponse(){
        return response;
    }

    protected void call(){
        try {
            StringBuilder requestURL = new StringBuilder(URL);
            requestURL.append(URLEncoder.encode("Signature", FORMAT_ENCODE)).append("=").append(vodAPIRequest.getSignature());
            for (Map.Entry<String, Object> e : allParam.entrySet()) {
                requestURL.append("&").append(SignatureGenerator.percentEncode(e.getKey())).
                        append("=").
                        append(SignatureGenerator.percentEncode(e.getValue().toString()));
            }
            Request request = new RequestBuilder().get().url(requestURL.toString()).build();
            System.out.println(requestURL.toString());
            JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper)manager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            JsonNode node = wrapper.convertBody();
            if(node!=null){
                Class<? extends VodResponse> targetClass = null;
                switch (requestType){
                    case GET:
                        if(Action.GetVideoPlayAuth.name().equalsIgnoreCase(vodAPIRequest.getAction()))
                            targetClass = VodPlayAuthResponse.class;
                        else
                            targetClass = VodInfoResponse.class;
                        break;
                    case GETLIST:
                        targetClass = VodInfosResponse.class;
                        break;
                    case DELETE:
                    case UPDATE:
                        targetClass = VodResponse.class;
                        break;
                    case GETPLAYINFO:
                        targetClass = VodPlayUrlInfoResponse.class;
                }
                response = JsonConverter.toObject(JsonConverter.toJson(node), targetClass);
                //增加回调功能
                if(callback!=null){
                    if(response!=null){
                        if("0".equalsIgnoreCase(response.getCode())){
                            callback.onSuccess(response);
                        }else {
                            callback.onFail(response);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void allParam(){
        if(commonParam!=null&&selfParam!=null){
            allParam = new HashMap<>(commonParam);
            allParam.putAll(selfParam);
        }
    }

    @Override
    public Map<String, Object> getAllParam(){
        return allParam;
    }
}
