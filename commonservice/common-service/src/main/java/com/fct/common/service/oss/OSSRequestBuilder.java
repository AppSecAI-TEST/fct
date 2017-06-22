package com.fct.common.service.oss;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class OSSRequestBuilder {

    private OSSRequest ossRequest;
    private static OSSRequestBuilder builder;

    public static OSSRequestBuilder builder(){
        builder = new OSSRequestBuilder();
        builder.setOssRequest(new OSSRequest());
        return builder;
    }

    public OSSRequestBuilder bucketName(String bucketName){
        preValidate();
        builder.getOssRequest().setBucketName(bucketName);
        return builder;
    }

    public OSSRequestBuilder key(String key){
        preValidate();
        builder.getOssRequest().setKey(key);
        return builder;
    }

    public OSSRequestBuilder ossClient(OSSClient ossClient){
        preValidate();
        builder.getOssRequest().setOssClient(ossClient);
        return builder;
    }

    public OSSRequestBuilder file(File file){
        preValidate();
        builder.getOssRequest().setFile(file);
        return builder;
    }

    public OSSRequest build(){
        postValidate();
        return builder.getOssRequest();
    }

    private void preValidate(){
        if (builder.getOssRequest()==null)
            throw new IllegalArgumentException("ossRequest should not be null");
    }

    private void postValidate(){
        if(StringUtils.isEmpty(builder.getOssRequest().getBucketName()))
            throw new IllegalArgumentException("bucketName should not be null");

        if(StringUtils.isEmpty(builder.getOssRequest().getKey()))
            throw new IllegalArgumentException("key should not be null");

        if(builder.getOssRequest().getOssClient()==null)
            throw new IllegalArgumentException("ossClient should not be null");

        if(builder.getOssRequest().getFile()==null)
            throw new IllegalArgumentException("file should not be null");
    }
}
