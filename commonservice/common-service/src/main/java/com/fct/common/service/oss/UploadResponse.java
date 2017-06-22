package com.fct.common.service.oss;

import com.aliyun.oss.model.PutObjectResult;
import lombok.Data;

import java.util.Map;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class UploadResponse{
    /**
     * 返回文件的etag
     */
    private String etag;
    /**
     * 文件对应的外链
     */
    private String url;

    /**
     * 这个对象主要是为了以后做扩展用, 目前只是用oss来上传图像
     * 没有断点续传, 也没有服务器级别的回调
     */
    private PutObjectResult result;
    /**
     * 请求文件路径和名称
     */
    private String key;

    /**
     * 返回文件uuid
     */
    private String returnKey;

    private Map<String, String> userMetaData;

    /**
     * 返回码 0 成功 1000 失败
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;
}
