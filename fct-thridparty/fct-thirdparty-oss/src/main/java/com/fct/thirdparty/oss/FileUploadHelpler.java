package com.fct.thirdparty.oss;

import com.aliyun.oss.OSSClient;
import com.fct.thirdparty.oss.builder.OSSRequestBuilder;
import com.fct.thirdparty.oss.callback.OSSCallback;
import com.fct.thirdparty.oss.factory.OSSClientFactory;
import com.fct.thirdparty.oss.request.OSSRequest;
import com.fct.thirdparty.oss.response.UploadResponse;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nick on 2017/5/12.
 */
public class FileUploadHelpler {

    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private OSSClient ossClient;

    public FileUploadHelpler(String bucketName, String accessKeyId,
                             String accessKeySecret, String endpoint){
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endpoint = endpoint;
    }

    private ExecutorService pool = Executors.newFixedThreadPool(20);

    public UploadResponse uploadFile(File file, String fileName){
        try {
            fileCheck(file, fileName);
            OSSRequestBuilder builder = OSSRequestBuilder.builder();
            OSSCallback callback = new OSSCallback(bucketName, fileName, ossClient);
            OSSRequest request = builder.bucketName(bucketName).
                    ossClient(ossClient).
                    file(file).
                    key(fileName).
                    callBack(callback).build();
            Future<UploadResponse> future = pool.submit(new OSS(request));
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initOssClient(){
        OSSClientFactory ossClientFactory = new OSSClientFactory(endpoint, accessKeyId, accessKeySecret);
        this.ossClient = ossClientFactory.getOSSClient();
    }

    private void fileCheck(File file, String name){
        if(file == null)
            throw new IllegalArgumentException("file should not be empty");

        if(file.length() > 5 * 1024 * 1024L){
            throw new IllegalArgumentException("file size should not over than 5M");
        }

        if(!file.getName().equalsIgnoreCase(name)){
            throw new IllegalArgumentException("file name should equals given name");
        }
    }
}
