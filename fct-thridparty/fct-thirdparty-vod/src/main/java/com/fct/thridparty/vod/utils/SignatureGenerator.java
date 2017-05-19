package com.fct.thridparty.vod.utils;

import com.fct.thridparty.vod.request.*;

/**
 * Created by nick on 2017/5/19.
 * 签名生成器
 */
public class SignatureGenerator {

    public static String generator(VodAPIRequest request, String accessKeySecret){
        if(request instanceof VodAPIDeleteRequest){
            VodAPIDeleteRequest deleteRequest = (VodAPIDeleteRequest)request;
        }else if(request instanceof VodAPIGetInfoRequest){
            VodAPIGetInfoRequest getRequest = (VodAPIGetInfoRequest)request;
        }else if(request instanceof VodAPIUpdateRequest){
            VodAPIUpdateRequest updateRequest = (VodAPIUpdateRequest)request;
        }else if(request instanceof VodAPIUploadRequest){
            VodAPIUploadRequest uploadRequest = new VodAPIUploadRequest();
        }
        return null;
    }
}
