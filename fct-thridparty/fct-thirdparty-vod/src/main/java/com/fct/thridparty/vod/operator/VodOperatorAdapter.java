package com.fct.thridparty.vod.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fct.common.utils.UUIDUtil;
import com.fct.thirdparty.http.builder.RequestBuilder;
import com.fct.thirdparty.http.entity.JsonNodeResponseWrapper;
import com.fct.thirdparty.http.util.JsonConverter;
import com.fct.thridparty.vod.response.VodResponse;
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

    public void setAction(String action){
        vodAPIRequest.setAction(action);
        selfParam.put("Action", action);
    }

    public void commonParam(String accessKeyId){
        initCommonParam(accessKeyId);
    }

    private void initCommonParam(String accessKeyId){
        commonParam = Maps.newHashMap();
        setFormat("JSON");
        setVersion("2017-03-21");
        setAccessKeyId(accessKeyId);
        setSignatureMethod("HMAC-SHA1");
        setSignatureNonce(UUIDUtil.generateUUID());
        setTimestamp(ISO8601TimeFormate(new Date()));
        setSignatureVersion("1.0");
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
                        append(SignatureGenerator.percentEncode((String)e.getValue()));
            }
            Request request = new RequestBuilder().get().url(requestURL.toString()).build();
            JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper)manager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            JsonNode node = wrapper.convertBody();
            if(node!=null)
                response = JsonConverter.toObject(JsonConverter.toJson(node), VodResponse.class);
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
