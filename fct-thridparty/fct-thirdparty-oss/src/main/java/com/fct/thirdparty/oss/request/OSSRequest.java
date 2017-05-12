package com.fct.thirdparty.oss.request;

import com.aliyun.oss.OSSClient;
import com.fct.thirdparty.oss.callback.OSSCallback;
import lombok.Data;

import java.io.File;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class OSSRequest {
    private String bucketName;
    private String key;
    private OSSCallback callback;
    private OSSClient ossClient;
    private File file;
}
