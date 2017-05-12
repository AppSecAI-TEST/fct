package com.fct.api.web.config;

import com.fct.thirdparty.oss.FileUploadHelpler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by ningyang on 2017/5/12.
 */
@Configuration
public class AliyunOSSConfig {

    @Autowired
    private Environment env;

    @Bean
    public FileUploadHelpler fileUploadHelpler(){
        String bucketName = env.getProperty("aliyun.oss.bucket.name");
        String accessKeyId = env.getProperty("aliyun.oss.access.key.id");
        String accessKeySecret = env.getProperty("aliyun.oss.access.key.secret");
        String endpoint = env.getProperty("aliyun.oss.endpoint");
        FileUploadHelpler helper = new FileUploadHelpler(bucketName, accessKeyId,
                accessKeySecret, endpoint);
        return helper;
    }
}
