package com.fct.thirdparty.oss;

import com.fct.thirdparty.oss.response.UploadResponse;
import org.junit.Test;

import java.io.File;

/**
 * Created by nick on 2017/5/12.
 */
public class OSSTest {

    private String bucketName = "fct-nick";
    private String accessKeyId = "LTAId0h3QAgdPmXT";
    private String accessKeySecret = "cLMJrTzs1APvyXehfjOQUsYq66A3Wc";
    private String endpoint = "http://oss-cn-shanghai.aliyuncs.com/";

    @Test
    public void uploadFileTest(){
        FileUploadHelpler helpler = new FileUploadHelpler(bucketName, accessKeyId,
                accessKeySecret, endpoint, null, 10);
        String path = "/Users/ningyang/Documents/haha.png";
        File file = new File(path);
        UploadResponse response = helpler.uploadFile(file, "haha.png");
        System.out.println(response.getUrl());
    }
}
