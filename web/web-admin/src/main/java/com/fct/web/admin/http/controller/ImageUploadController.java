package com.fct.web.admin.http.controller;

import com.fct.thirdparty.oss.FileOperatorHelper;
import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import com.fct.web.admin.http.json.JsonListResponseEntity;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by nick on 2017/6/5.
 */
@RestController
@RequestMapping("/imageUpload")
public class ImageUploadController {

    @Autowired
    private FileOperatorHelper fileOperatorHelper;

    /**
     * 批量上传文件
     * @param request
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JsonListResponseEntity<String> batchUploadImgs(HttpServletRequest request){
        JsonListResponseEntity<String> responseEntity = new JsonListResponseEntity<>();
        List<File> fileList = Lists.newArrayList();
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        List<String> keys = Lists.newArrayList();
        List<String> imgUrls = Lists.newArrayList();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        try{
            if(files!=null&&files.size()>0){
                for(MultipartFile multipartFile: files){
                    byte[] bytes = multipartFile.getBytes();
                    File f = new File(multipartFile.getOriginalFilename());
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                    stream.write(bytes);
                    stream.close();
                    keys.add(multipartFile.getOriginalFilename());
                    fileList.add(f);
                }
            }
            fileServiceRequest.setFiles(fileList);
            fileServiceRequest.setKeys(keys);
            fileServiceRequest.setUserMetaData(new HashedMap());
            List<UploadResponse> responses = fileOperatorHelper.uploadFile(fileServiceRequest);
            if(responses!=null&&responses.size()>0){
                for(UploadResponse response: responses){
                    imgUrls.add(response.getUrl());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        responseEntity.setContent(imgUrls);
        return responseEntity;
    }
}
