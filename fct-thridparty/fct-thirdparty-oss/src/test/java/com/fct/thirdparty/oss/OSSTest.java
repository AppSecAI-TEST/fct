package com.fct.thirdparty.oss;

import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        String path1 = "/Users/ningyang/Documents/haha1.png";
        String path2 = "/Users/ningyang/Documents/haha2.png";
        File file = new File(path);
        File file1 = new File(path1);
        File file2 = new File(path2);
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        List<File> files = new ArrayList();
        List<String> keys = new ArrayList();
        files.add(file);
        files.add(file1);
        files.add(file2);
        keys.add("haha.png");
        keys.add("haha1.png");
        keys.add("haha2.png");
        fileServiceRequest.setFiles(files);
        fileServiceRequest.setKeys(keys);
        fileServiceRequest.setUserMetaData(new HashedMap());
        FileOperatorHelper helper = new FileOperatorHelper(bucketName, accessKeyId,
                accessKeySecret, endpoint);
        List<UploadResponse> responses = helper.uploadFile(fileServiceRequest);
        if(responses!=null&&responses.size()>0){
            for(UploadResponse response: responses){
                System.out.println(response.getUrl());
            }
        }
    }
}
