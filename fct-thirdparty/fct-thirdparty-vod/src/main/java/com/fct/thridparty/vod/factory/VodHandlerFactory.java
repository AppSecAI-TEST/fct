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
        VodAPIRequestBuilder builder = new VodAPIRequestBuilder(requestType);
        switch (requestType){
            case GET:
                handler = new VodGetInfoHandler(requestType, manager, builder);
                break;
            case DELETE:
                handler = new VodDeleteHandler(requestType, manager, builder);
                break;
            case UPLOAD:
                handler = new VodUploadHandler(requestType, builder);
                break;
            case UPDATE:
                handler = new VodUpdateHandler(requestType, manager, builder);
                break;
            case GETLIST:
                handler = new VodGetVideosHandler(requestType, manager, builder);
                break;
        }
        return handler;
    }
}
