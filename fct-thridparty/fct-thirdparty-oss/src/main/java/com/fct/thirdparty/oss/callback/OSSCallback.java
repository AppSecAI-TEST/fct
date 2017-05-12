package com.fct.thirdparty.oss.callback;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

/**
 * Created by nick on 2017/5/12.
 */
public class OSSCallback {

    private OSSClient ossClient;
    private String bucketName;
    private String key;

    public OSSCallback(String bucketName, String key, OSSClient ossClient){
        this.bucketName = bucketName;
        this.key = key;
        this.ossClient = ossClient;
    }

    public String callBack(){
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        return ossClient.generatePresignedUrl(request).toString();
    }
}
