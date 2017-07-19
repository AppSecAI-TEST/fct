package com.fct.vod.test;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.fct.thridparty.vod.AliyunVod;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.response.VodResponse;
import com.fct.thridparty.vod.response.VodUploadResponse;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.File;
import java.util.Map;


/**
 * Created by ningyang on 2017/5/23.
 */
public class UploadTest {

    private String accessKeyId = "LTAIhVymglNdvlXM";
    private String accessKeySecret = "Ml6DD0eNolZWB45w0OTTCPlprX4uHg";

    /**
     * 阿里云官网的上传例子
     */
    @Test
    public void uploadVodTest(){
        String fileName = "/User/ningyang/desktop/xx.mp4";
        String title = "视频标题";
        //构造上传请求实例
        UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
        //视频分类ID
        request.setCateId(0);
        //视频标签,多个用逗号分隔
        request.setTags("标签1,标签2");
        //视频自定义封面URL
        request.setCoverURL("http://cover.sample.com/sample.jpg");
        //设置上传完成后的回调URL
//        request.setCallback("http://callback.sample.com");
        //可指定分片上传时每个分片的大小，默认为10M字节
        request.setPartSize(10 * 1024 * 1024L);
        //可指定分片上传时的并发线程数，默认为1 (注: 该配置会占用服务器CPU资源，需根据服务器情况指定）
        request.setTaskNum(1);
        request.setDescription("视频描述");
        //设置是否使用水印
        request.setIsShowWaterMark(true);
//        try {
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);
        //上传成功后返回视频ID
        System.out.print(response.getVideoId());
    }

    /**
     * 自己框架封装后的上传例子
     */
    @Test
    public void selfUploadVodTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        File file = new File("/Users/nick/Desktop/xx.mp4");
        selfParam.putIfAbsent("title", "视频测试");
        selfParam.putIfAbsent("file", file);
        selfParam.put("coverUrl", "http://img.wdjky.com/0fd117f2398341500369243475.jpg");
        selfParam.put("tags","运动");
        selfParam.put("description","描述");
        selfParam.put("cateId", 990982960);
        //selfParam.put("callbackUrl","http://callback.sample.com"); 如果有服务器回调地址可以填入, 不过我自己实现了回调不过是同步的不是异步的
        //对于视频上传AliyunVod对象的链式调用不需要设置Action和签名, 因为封装了阿里云自己的实现
        VodResponse response = new AliyunVod(RequestType.UPLOAD, accessKeyId, accessKeySecret).
                                    buildRequest().
                                    withSelfParam(selfParam).
                                    run().
                                    response();
        if(response instanceof VodUploadResponse){
            VodUploadResponse uploadResponse = (VodUploadResponse) response;
            System.out.println(uploadResponse.getCode() + " : " + uploadResponse.getMessage() + " : " + uploadResponse.getVideoId());
        }

    }
}
