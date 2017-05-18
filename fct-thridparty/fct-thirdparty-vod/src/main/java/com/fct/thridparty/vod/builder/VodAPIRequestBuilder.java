package com.fct.thridparty.vod.builder;

import com.fct.thridparty.vod.request.VodAPIRequest;
import org.apache.commons.lang.StringUtils;

/**
 * Created by nick on 2017/5/18.
 */
public class VodAPIRequestBuilder {

    private VodAPIRequest request;

    public VodAPIRequestBuilder(){
        request = new VodAPIRequest();
    }

    public VodAPIRequestBuilder accessKeyId(String accessKeyId){
        request.setAccessKeyId(accessKeyId);
        return this;
    }

    public VodAPIRequestBuilder format(String format){
        request.setFormat(format);
        return this;
    }

    public VodAPIRequestBuilder version(String version){
        request.setVersion(version);
        return this;
    }

    public VodAPIRequestBuilder signatureMethod(String signatureMethod){
        if(StringUtils.isEmpty(signatureMethod)){
            signatureMethod = "HMAC-SHA1";
        }
        request.setSignatureMethod(signatureMethod);
        return this;
    }
}
