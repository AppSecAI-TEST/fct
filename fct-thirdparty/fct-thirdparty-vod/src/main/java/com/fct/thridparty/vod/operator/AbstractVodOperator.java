package com.fct.thridparty.vod.operator;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.callback.Callback;
import com.fct.thridparty.vod.handler.VodHandler;
import com.fct.thridparty.vod.request.VodAPIRequest;
import com.fct.thridparty.vod.response.VodResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nick on 2017/5/19.
 */
public abstract class AbstractVodOperator implements VodOperator, VodHandler{

    protected VodAPIRequest vodAPIRequest;
    protected VodAPIRequestBuilder builder;
    protected VodResponse response;
    protected String accessKeySecret;
    protected RequestType requestType;
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    protected Callback callback;

    protected static final String URL = "http://vod.cn-shanghai.aliyuncs.com?";

    protected HttpRequestExecutorManager manager;

    protected Map<String, Object> commonParam;
    protected Map<String, Object> selfParam;
    protected Map<String, Object> allParam;

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public abstract void buildRequest();

    public VodAPIRequest getRequest(){
        return vodAPIRequest;
    }

    protected void setSignature(String signature){
        vodAPIRequest.setSignature(signature);
        commonParam.put("Signature", signature);
    }

    /**
     * 把Date转换成ISO8601标准时间
     * @param date
     * @return
     */
    public String ISO8601TimeFormate(Date date){
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    @Override
    public void setCallback(Callback callback){
        this.callback = callback;
    }
}
