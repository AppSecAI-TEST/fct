package com.fct.thirdparty.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.fct.thirdparty.oss.callback.OSSCallback;
import com.fct.thirdparty.oss.request.OSSRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by nick on 2017/5/12.
 */
public final class OSS implements Callable<UploadResponse>{

    private OSSRequest request;

    public OSS(OSSRequest request){
        this.request = request;
    }

    @Override
    public UploadResponse call() throws Exception {
        if(!request.getOssClient().doesBucketExist(request.getBucketName()))
            request.getOssClient().createBucket(request.getBucketName());
        UploadResponse response = new UploadResponse();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(request.getFile().length());
        metadata.setContentType("image/*");
        PutObjectRequest putObjectRequest = new PutObjectRequest(request.getBucketName(), request.getKey(), request.getFile(), metadata);
        PutObjectResult putObjectResult = request.getOssClient().putObject(putObjectRequest);
        if(!StringUtils.isEmpty(putObjectResult.getETag())&&request.getCallback()!=null){
            String url = request.getCallback().callBack();
            response.setEtag(putObjectResult.getETag());
            if(!StringUtils.isEmpty(url)&&url.startsWith("http")){
                response.setResult(putObjectResult);
                response.setCode(0);
                response.setUrl(url);
                response.setMsg("上传成功");
                return response;
            }
        }
        response.setCode(1000);
        response.setMsg("上传失败");
        return response;
    }
}
