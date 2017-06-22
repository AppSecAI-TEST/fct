package com.fct.common.service.oss;

import com.aliyun.oss.OSSClient;
import lombok.Data;

import java.io.File;
import java.util.Map;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class OSSRequest {
    private String bucketName;
    private String key;
    private OSSClient ossClient;
    private File file;
    private Map<String, String> userMetaData;
}
