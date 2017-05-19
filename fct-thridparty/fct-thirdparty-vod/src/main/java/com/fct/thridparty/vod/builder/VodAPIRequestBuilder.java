package com.fct.thridparty.vod.builder;

import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.request.*;
import org.apache.commons.lang.StringUtils;

/**
 * Created by nick on 2017/5/18.
 */
public class VodAPIRequestBuilder {

    private VodAPIRequest request;

    public VodAPIRequestBuilder(RequestType requestType){
        switch (requestType){
            case DELETE:
                request = new VodAPIDeleteRequest();
                break;
            case UPLOAD:
                request = new VodAPIUploadRequest();
                break;
            case GET:
                request = new VodAPIGetInfoRequest();
                break;
            case UPDATE:
                request = new VodAPIUpdateRequest();
        }
        if(request!=null)
            request.setVersion("2017-03-21");
    }

    public VodAPIRequestBuilder accessKeyId(String accessKeyId){
        request.setAccessKeyId(accessKeyId);
        return this;
    }

    public VodAPIRequestBuilder format(String format){
        request.setFormat(format);
        return this;
    }

    public VodAPIRequestBuilder signatureMethod(String signatureMethod){
        if(StringUtils.isEmpty(signatureMethod)){
            signatureMethod = "HMAC-SHA1";
        }
        request.setSignatureMethod(signatureMethod);
        return this;
    }

    public VodAPIRequestBuilder timestamp(String timestamp){
        request.setTimestamp(timestamp);
        return this;
    }

    public VodAPIRequestBuilder signatureVersion(String signatureVersion){
        request.setSignatureVersion(signatureVersion);
        return this;
    }

    public VodAPIRequestBuilder signatureNonce(String signatureNonce){
        request.setSignatureNonce(signatureNonce);
        return this;
    }

    public VodAPIRequest build(){
        return request;
    }
}
