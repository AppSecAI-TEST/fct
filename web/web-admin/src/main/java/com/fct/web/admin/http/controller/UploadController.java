package com.fct.web.admin.http.controller;

import com.fct.common.data.entity.ImageSource;
import com.fct.common.interfaces.CommonService;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
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

    @Autowired
    private CommonService commonService;

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

        ImageSource imageSource = new ImageSource();

        try{
            if(files!=null&&files.size()>0){
                for(MultipartFile multipartFile: files){
                    byte[] bytes = multipartFile.getBytes();
                    String originalName = multipartFile.getOriginalFilename();
                    File f = new File(originalName);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                    stream.write(bytes);
                    stream.close();
                    keys.add(originalName);
                    fileList.add(f);

                    String suffix = originalName.split("\\.")[1];

                    BufferedImage sourceImg = ImageIO.read(new FileInputStream(f));

                    Float length = new Float(f.length() / 1024.0); // 源图大小

                    imageSource = new ImageSource();
                    imageSource.setCategoryId(0);
                    imageSource.setWidth(sourceImg.getWidth());// 源图宽度
                    imageSource.setHeight(sourceImg.getHeight());// 源图高度
                    imageSource.setFileType(suffix);
                    imageSource.setFileLength(length);
                    imageSource.setOriginalName(originalName);

                }
            }
            fileServiceRequest.setFiles(fileList);
            fileServiceRequest.setKeys(keys);
            fileServiceRequest.setUserMetaData(new HashedMap());
            responses = fileOperatorHelper.uploadFile(fileServiceRequest);

            //responseEntity.setContent(imgUrls);
            if(responses != null) {
                UploadResponse response =responses.get(0);
                String imgUrl = "";
                //非编辑器模式，上传的图片
                try {
                    URL url = new URL(response.getUrl()); //只返回相对路径
                    imgUrl = url.getFile();
                    imgUrl = imgUrl.substring(0,imgUrl.indexOf("@"));
                }
                catch (IOException exp)
                {
                    Constants.logger.error(exp.toString());
                }

                //imageSource.setName(response.getKey());
                /*String guid = imgUrl.substring(imgUrl.lastIndexOf('/')+1);
                guid = guid.substring(0,guid.indexOf('.'));*/

                imageSource.setUrl(imgUrl);
                imageSource.setGuid(response.getReturnKey());
                commonService.saveImageSource(imageSource);

                if(StringUtils.isEmpty(action))
                {
                    response.setUrl(imgUrl);
                }
                else {
                    response.setUrl(response.getUrl());
                }

                responseEntity.setData(response);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return responseEntity;
    }
}
