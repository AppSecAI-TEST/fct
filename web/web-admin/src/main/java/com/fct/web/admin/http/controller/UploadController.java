package com.fct.web.admin.http.controller;

import com.fct.common.data.entity.ImageSource;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.FileRequest;
import com.fct.common.interfaces.ImageResponse;
import com.fct.core.utils.ReturnValue;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 2017/6/5.
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public ReturnValue<Object> uploadImages(HttpServletRequest request){
        List<byte[]> fileList = Lists.newArrayList();
        List<ImageSource> imgList = new ArrayList<>();

        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        String action = request.getParameter("action");
        ReturnValue<Object> responseEntity =  new ReturnValue<>();

        try{
            if(files!=null&&files.size()>0){
                for(MultipartFile multipartFile: files){

                    byte[] bytes = multipartFile.getBytes();
                    String originalName = multipartFile.getOriginalFilename();
                    File f = new File(originalName);

                    ImageSource img = new ImageSource();
                    BufferedImage sourceImg = ImageIO.read(new FileInputStream(f));

                    Float length = new Float(f.length() / 1024.0); // 源图大小

                    img.setCategoryId(0);
                    img.setWidth(sourceImg.getWidth());// 源图宽度
                    img.setHeight(sourceImg.getHeight());// 源图高度
                    img.setFileLength(length);
                    img.setOriginalName(originalName);

                    fileList.add(bytes);
                    imgList.add(img);

                }
            }

            FileRequest fileRequest = new FileRequest();
            fileRequest.setFiles(fileList);
            fileRequest.setImages(imgList);
            fileRequest.setUserMetaData(new HashedMap());

            List<ImageResponse> responseList = commonService.uploadImage(fileRequest);

            if(responseList != null) {
                ImageResponse response =responseList.get(0);
                String imgUrl = "";
                //非编辑器模式，上传的图片
                if(!StringUtils.isEmpty(action))
                {
                    Constants constants = new Constants();
                    response.setUrl(constants.thumbnail(response.getUrl()));
                }

                responseEntity.setData(response);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return responseEntity;
    }
}
