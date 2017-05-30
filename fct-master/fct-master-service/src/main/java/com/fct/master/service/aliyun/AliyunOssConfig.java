package com.fct.master.service.aliyun;

import com.fct.thirdparty.oss.FileOperatorHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/30.
 */
@Configuration
public class AliyunOssConfig {

    @Value("aliyun.oss.bucket.name")
    private String bucketName;

    @Value("aliyun.oss.access.key.id")
    private String accessKeyId;

    @Value("aliyun.oss.access.key.secret")
    private String accessKeySecret;

    @Value("aliyun.oss.endpoint")
    private String endpoint;

    @Bean
    public FileOperatorHelper fileOperatorHelper(){
        FileOperatorHelper helper = new FileOperatorHelper(bucketName, accessKeyId, accessKeySecret, endpoint);
        return helper;
    }
}
