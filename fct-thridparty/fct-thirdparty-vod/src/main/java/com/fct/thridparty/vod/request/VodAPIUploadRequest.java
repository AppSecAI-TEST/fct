package com.fct.thridparty.vod.request;

import com.aliyun.vod.upload.req.UploadVideoRequest;
import lombok.Data;

import java.io.File;

/**
 * Created by nick on 2017/5/19.
 * 上传视频请求
 */
@Data
public class VodAPIUploadRequest extends VodAPIRequest {

    private String accessKeySecret;
    private File file;
    private String title;
    private UploadVideoRequest uploadVideoRequest;
    private String tags;
    private String coverUrl;
    private String description;
    private String callbackUrl;
    private int cateId;

    public void initUploadVideoRequest(){
        this.uploadVideoRequest = new UploadVideoRequest(getAccessKeyId(), accessKeySecret, title, file.getAbsolutePath());
        uploadVideoRequest.setCateId(cateId);
        //视频标签,多个用逗号分隔
        uploadVideoRequest.setTags(tags);
        //视频自定义封面URL
        uploadVideoRequest.setCoverURL(coverUrl);
        //设置上传完成后的回调URL
        uploadVideoRequest.setCallback(callbackUrl);
        //可指定分片上传时每个分片的大小，默认为10M字节
        uploadVideoRequest.setPartSize(10 * 1024 * 1024L);
        //可指定分片上传时的并发线程数，默认为1 (注: 该配置会占用服务器CPU资源，需根据服务器情况指定）
        uploadVideoRequest.setTaskNum(1);
        uploadVideoRequest.setDescription(description);
        //设置是否使用水印
        uploadVideoRequest.setIsShowWaterMark(true);
    }
}
