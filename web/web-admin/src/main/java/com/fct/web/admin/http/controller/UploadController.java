package com.fct.web.admin.http.controller;

import com.fct.thirdparty.oss.FileOperatorHelper;
import com.fct.thirdparty.oss.entity.FileServiceRequest;
import com.fct.thirdparty.oss.response.UploadResponse;
import com.fct.web.admin.http.json.JsonResponseEntity;
import com.fct.web.admin.utils.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by nick on 2017/6/5.
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private FileOperatorHelper fileOperatorHelper;

    /**
     * 批量上传文件
     * @param request
     * @return
     */
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public JsonResponseEntity<Object> batchUploadImgs(HttpServletRequest request){
        JsonResponseEntity<Object> responseEntity =  new JsonResponseEntity<>();
        List<File> fileList = Lists.newArrayList();
        FileServiceRequest fileServiceRequest = new FileServiceRequest();
        List<String> keys = Lists.newArrayList();
        List<String> imgUrls = Lists.newArrayList();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        String action = request.getParameter("action");

        List<UploadResponse> responses = null;
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
            responses = fileOperatorHelper.uploadFile(fileServiceRequest);
            if(responses!=null&&responses.size()>0){
                for(UploadResponse response: responses){
                    keys.add(response.getKey());
                    imgUrls.add(response.getUrl());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //responseEntity.setContent(imgUrls);
        if(responses != null) {
            UploadResponse response =responses.get(0);
            if(StringUtils.isEmpty(action))
            {
                //非编辑器模式，上传的图片
                try {
                    URL url = new URL(response.getUrl()); //只返回相对路径

                    response.setUrl(url.getFile());
                }
                catch (IOException exp)
                {
                    Constants.logger.error(exp.toString());
                }
            }
            response.setUrl(response.getUrl().substring(0,response.getUrl().indexOf("@")));
            responseEntity.setData(response);
        }
        return responseEntity;
    }
}
