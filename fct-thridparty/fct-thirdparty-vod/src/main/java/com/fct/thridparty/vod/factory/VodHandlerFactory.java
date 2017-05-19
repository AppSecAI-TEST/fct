package com.fct.thridparty.vod.factory;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.handler.*;

/**
 * Created by nick on 2017/5/19.
 */
public class VodHandlerFactory {

    private static VodHandler handler;

    public static VodHandler getHandler(RequestType requestType, HttpRequestExecutorManager manager){
        switch (requestType){
            case GET:
                handler = new VodGetInfoHandler(manager, new VodAPIRequestBuilder(requestType));
                break;
            case DELETE:
                handler = new VodDeleteHandler(manager, new VodAPIRequestBuilder(requestType));
                break;
            case UPLOAD:
                handler = new VodUploadHandler(manager, new VodAPIRequestBuilder(requestType));
                break;
            case UPDATE:
                handler = new VodUpdateHandler(manager, new VodAPIRequestBuilder(requestType));
                break;
        }
        return handler;
    }
}
