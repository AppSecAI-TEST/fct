package com.fct.thirdparty.oss;

import com.aliyun.oss.OSSClient;
import com.fct.thirdparty.oss.builder.OSSRequestBuilder;
import com.fct.thirdparty.oss.callback.OSSCallback;
import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.factory.OSSClientFactory;
import com.fct.thirdparty.oss.request.OSSRequest;
import com.fct.thirdparty.oss.response.UploadResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nick on 2017/5/12.
 */
public class FileOperatorHelper {

    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private OSSClient ossClient;
    private OSSCallback callback;
    private Integer threadSize = 20;

    /**
     * 管理OSS上传任务的线程池 默认20个
     */
    private ExecutorService pool;

    public FileOperatorHelper(String bucketName, String accessKeyId,
                              String accessKeySecret, String endpoint,
                              OSSCallback callback, Integer threadSize){

        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endpoint = endpoint;
        this.callback = callback;
        if(threadSize!=null|| threadSize > 0)
            this.threadSize = threadSize;
        pool = Executors.newFixedThreadPool(this.threadSize);
        initOssClient();
    }

    /**
     * 上传文件
     * @param fileServiceRequest
     * @return
     */
    public UploadResponse uploadFile(FileServiceRequest fileServiceRequest){
        try {
            fileCheck(fileServiceRequest.getFile(), fileServiceRequest.getKey());
            OSSRequestBuilder builder = OSSRequestBuilder.builder();
            OSSRequest request = builder.bucketName(bucketName).
                                            ossClient(ossClient).
                                            file(fileServiceRequest.getFile()).
                                            key(fileServiceRequest.getKey()).
                                            callBack(callback).
                                            build();
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
            throw new IllegalArgumentException("上传文件不能为空");

        if(file.length() > 5 * 1024 * 1024L){
            throw new IllegalArgumentException("上传图片大小不能大于5M");
        }

        if(!file.getName().equalsIgnoreCase(name)){
            throw new IllegalArgumentException("上传文件和所给名称不一致");
        }

        if(!isImage(file)){
            throw new IllegalArgumentException("上传文件不是一个图片");
        }
    }

    /**
     * 判斷是否為圖片
     * @param imageFile
     * @return
     */
    private boolean isImage(File imageFile) {
        if (!imageFile.exists()) {
            return false;
        }
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }
}
