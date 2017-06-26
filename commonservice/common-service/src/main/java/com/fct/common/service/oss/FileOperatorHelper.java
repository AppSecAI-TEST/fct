package com.fct.common.service.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.fct.common.data.entity.ImageSource;
import com.fct.common.interfaces.FileRequest;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class FileOperatorHelper {

    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private OSSClient ossClient;
    private Integer threadSize = 20;

    /**
     * 管理OSS上传任务的线程池 默认20个
     */
    private ExecutorService pool;
    private final static String PREFIX = "fct/";
    private final static String ALGORITHM = "HmacSHA1";

    public FileOperatorHelper(String bucketName, String accessKeyId, String accessKeySecret, String endpoint){
        this(bucketName, accessKeyId, accessKeySecret, endpoint, 20);
    }

    public FileOperatorHelper(String bucketName, String accessKeyId,
                              String accessKeySecret, String endpoint,
                              Integer threadSize){
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endpoint = endpoint;
        if(threadSize!=null|| threadSize > 0)
            this.threadSize = threadSize;
        pool = Executors.newFixedThreadPool(this.threadSize);
        initOssClient();
    }

    /**
     * 上传文件
     * @return
     */
    public UploadResponse uploadFile(byte[] file,String fileName){
        try {
            OSSRequestBuilder builder = OSSRequestBuilder.builder();
            OSSRequest request = builder.bucketName(bucketName).
                    ossClient(ossClient).
                    file(file).
                    key(buildKey(fileName)).
                    build();
            Future<UploadResponse> future = pool.submit(new OSS(request));
            UploadResponse response = future.get();
            return response;

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

    /**
     * 批量删除文件
     * @param request
     * @return
     */
    public DeleteResponse deleteFile(FileRequest request){
        List<String> lsKey =  new ArrayList<>();
        for (ImageSource img: request.getImages()
             ) {
            lsKey.add(img.getName());
        }
        DeleteResponse deleteResponse = new DeleteResponse();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.withKeys(lsKey);
        ossClient.deleteObject(deleteObjectsRequest);
        deleteResponse.setUserMetaData(request.getUserMetaData());
        deleteResponse.setKeys(lsKey);
        deleteResponse.setCode(0);
        deleteResponse.setMsg("删除文件成功");
        return deleteResponse;
    }

    public void fileCheck(File file){

        if(file.length() > 5 * 1024 * 1024L){
            throw new IllegalArgumentException("上传图片大小不能大于5M");
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

    private String buildKey(String fileName){
        return KeyBuilder.buildKey(fileName);
    }
}
