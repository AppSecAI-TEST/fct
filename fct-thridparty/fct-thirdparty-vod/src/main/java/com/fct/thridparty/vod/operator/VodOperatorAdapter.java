package com.fct.thridparty.vod.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fct.common.utils.UUIDUtil;
import com.fct.thirdparty.http.builder.RequestBuilder;
import com.fct.thirdparty.http.entity.JsonNodeResponseWrapper;
import com.fct.thirdparty.http.util.JsonConverter;
import com.fct.thridparty.vod.response.VodResponse;
import com.fct.thridparty.vod.utils.SignatureGenerator;
import com.squareup.okhttp.Request;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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

    public void setSignature(String signature){
        vodAPIRequest.setSignature(signature);
    }

    public void setAction(String action){
        vodAPIRequest.setAction(action);
    }

    protected String buildSignature(){
        return SignatureGenerator.generator(vodAPIRequest, accessKeySecret);
    }
    public void commonParam(String accessKeyId){
        setFormat("JSON");
        setVersion("2017-03-21");
        setAccessKeyId(accessKeyId);
        setSignatureMethod("HMAC-SHA1");
        setSignatureNonce(UUIDUtil.generateUUID());
        setTimestamp(ISO8601TimeFormate(new Date()));
        setSignatureVersion("1.0");
    }

    protected abstract void selfParam(Map<String, Object> selfParam);

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

    public void call(){
        try {
            Request request = new RequestBuilder().get().url(URL).params(getParams()).build();
            JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper)manager.newCall(request).run().as(JsonNodeResponseWrapper.class);
            System.out.println(request.httpUrl().toString());
            JsonNode node = wrapper.convertBody();
            if(node!=null)
                response = JsonConverter.toObject(JsonConverter.toJson(node), VodResponse.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
