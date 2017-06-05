package com.fct.web.admin.config;


import com.fct.thirdparty.oss.FileOperatorHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ningyang on 2017/5/12.
 */
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

    @Bean
    public FileOperatorHelper fileUploadHelper(){
        FileOperatorHelper helper = new FileOperatorHelper(bucketName, accessKeyId,
                accessKeySecret, endpoint);
        return helper;
    }
}
