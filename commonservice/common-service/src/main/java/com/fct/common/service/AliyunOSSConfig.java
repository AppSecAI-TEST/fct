package com.fct.common.service;

import com.fct.common.service.oss.FileOperatorHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/12.
 */
@Data
@Configuration
public class AliyunOSSConfig {

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.accesskeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accesskeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.domain}")
    private String domain;

    @Bean
    public FileOperatorHelper fileUploadHelper(){
        FileOperatorHelper helper = new FileOperatorHelper(bucketName, accessKeyId,
                accessKeySecret, endpoint);
        return helper;
    }
}
