package com.fct.thirdparty.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.fct.thirdparty.oss.builder.OSSRequestBuilder;
import com.fct.thirdparty.oss.callback.OSSCallback;
import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.factory.OSSClientFactory;
import com.fct.thirdparty.oss.request.OSSRequest;
import com.fct.thirdparty.oss.response.DeleteResponse;
import com.fct.thirdparty.oss.response.UploadResponse;
import lombok.Data;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.xml.crypto.dsig.SignatureMethod;
import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
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
    private OSSCallback callback;
    private Integer threadSize = 20;

    /**
     * 管理OSS上传任务的线程池 默认20个
     */
    private ExecutorService pool;
    private final static String PREFIX = "fct/";
    private final static String ALGORITHM = "HmacSHA1";

    public FileOperatorHelper(String bucketName, String accessKeyId, String accessKeySecret, String endpoint){
        this(bucketName, accessKeyId, accessKeySecret, endpoint, null, 20);
    }

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
     * 批量上传文件
     * @param fileServiceRequest
     * @return
     */
    public List<UploadResponse> uploadFile(FileServiceRequest fileServiceRequest){
        List<UploadResponse> responses = new ArrayList<>();
        try {
            getCallBack(fileServiceRequest);
            fileCheck(fileServiceRequest.getFiles(), fileServiceRequest.getKeys());
            for(int i=0; i<fileServiceRequest.getFiles().size();i++){
                OSSRequestBuilder builder = OSSRequestBuilder.builder();
                OSSRequest request = builder.bucketName(bucketName).
                        ossClient(ossClient).
                        file(fileServiceRequest.getFiles().get(i)).
                        key(buildKey(fileServiceRequest.getKeys().get(i))).
                        callBack(callback).
                        build();
                Future<UploadResponse> future = pool.submit(new OSS(request));
                UploadResponse response = future.get();
                responses.add(response);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return responses;
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
    public DeleteResponse deleteFile(FileServiceRequest request){
        DeleteResponse deleteResponse = new DeleteResponse();
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.withKeys(request.getKeys());
        ossClient.deleteObject(deleteObjectsRequest);
        getCallBack(request);
        deleteResponse.setUserMetaData(request.getUserMetaData());
        deleteResponse.setKeys(request.getKeys());
        deleteResponse.setCode(0);
        deleteResponse.setMsg("删除文件成功");
        if(callback!=null)
            callback.onSuccess(deleteResponse);
        return deleteResponse;
    }

    private void fileCheck(List<File> files, List<String> keys){

        if(files == null)
            throw new IllegalArgumentException("上传文件不能为空");

        if(keys == null)
            throw new IllegalArgumentException("上传文件名称不能为空");

        if(files.size()!=keys.size())
            throw new IllegalArgumentException("上传文件的个数和名称个数不一致");

        for(int i=0; i<files.size(); i++){

            if(files.get(i).length() > 5 * 1024 * 1024L){
                throw new IllegalArgumentException("上传图片大小不能大于5M");
            }

            if(!isImage(files.get(i))){
                throw new IllegalArgumentException("上传文件不是一个图片");
            }
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

    private OSSCallback getCallBack(FileServiceRequest fileServiceRequest){
        if(callback==null)
            callback = fileServiceRequest.getCallback();
        return callback;
    }

    private String buildKey(String fileName){
        return KeyBuilder.buildKey(fileName);
    }
}
