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
    private String endpoint = "fct-nick.oss-cn-shanghai.aliyuncs.com";

    @Test
    public void uploadFileTest(){
        FileUploadHelpler helpler = new FileUploadHelpler(bucketName, accessKeyId, accessKeySecret, endpoint);
        String path = "/Users/nick/Document/solr.pptx";
        File file = new File(path);
        UploadResponse response = helpler.uploadFile(file, "solr.pptx");
        System.out.println(response.getUrl());
    }
}
