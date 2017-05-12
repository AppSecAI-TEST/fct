package com.fct.thirdparty.oss;

import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.fct.thirdparty.oss.request.OSSRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import org.apache.commons.lang.StringUtils;

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
        //如果bucket没有创建就创建一个
        if(!request.getOssClient().doesBucketExist(request.getBucketName()))
            request.getOssClient().createBucket(request.getBucketName());

        UploadResponse response = new UploadResponse();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(request.getFile().length());
        metadata.setContentType("image/*");
        PutObjectRequest putObjectRequest = new PutObjectRequest(request.getBucketName(), request.getKey(), request.getFile(), metadata);
        PutObjectResult putObjectResult = request.getOssClient().putObject(putObjectRequest);
        if(!StringUtils.isEmpty(putObjectResult.getETag())){
            //if callback exist perform callback
            if(request.getCallback()!=null){
                request.getCallback().callBack();
            }
            response.setEtag(putObjectResult.getETag());
            response.setResult(putObjectResult);
            response.setCode(0);
            response.setUrl(writeUrl(request.getBucketName(), request.getKey(), request.getOssClient().getEndpoint().toString()));
            response.setMsg("上传成功");
            return response;
        }
        response.setCode(1000);
        response.setMsg("上传失败");
        return response;
    }

    /**
     * 生成文件url
     * @param key
     * @param endpoint
     * @return
     */
    private String writeUrl(String bucketName, String key, String endpoint){
        StringBuffer sb = new StringBuffer();
        String[] splits = endpoint.split("http://");
        sb.append("http://").
                append(bucketName).
                append(".").
                append(splits[1]).
                append(key);
        return sb.toString();
    }
}
