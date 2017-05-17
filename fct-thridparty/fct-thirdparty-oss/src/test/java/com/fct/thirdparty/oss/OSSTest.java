package com.fct.thirdparty.oss;

import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import org.apache.commons.collections.map.HashedMap;
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
        String path = "/Users/ningyang/Documents/haha.png";
        File file = new File(path);
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        fileServiceRequest.setFile(file);
        fileServiceRequest.setKey("haha.png");
        fileServiceRequest.setUserMetaData(new HashedMap());
        FileOperatorHelper helper = new FileOperatorHelper(bucketName, accessKeyId,
                accessKeySecret, endpoint, null, 10);
        UploadResponse response = helper.uploadFile(fileServiceRequest);
        System.out.println(response.getUrl());
    }
}
