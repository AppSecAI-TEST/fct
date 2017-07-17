package com.fct.api.web.http.controller;

import com.fct.api.web.config.FctConfig;
import com.fct.common.data.entity.ImageSource;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.FileRequest;
import com.fct.common.interfaces.ImageResponse;
import com.fct.core.utils.ReturnValue;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nick on 2017/6/5.
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private FctConfig fctConfig;

    @RequestMapping(value = "image", method = RequestMethod.POST)
    public ReturnValue<Object> uploadImages(HttpServletRequest request){

        this.memberAuth();

        List<byte[]> fileList = Lists.newArrayList();
        List<ImageSource> imgList = new ArrayList<>();

        MultipartFile multipartFile = ((MultipartHttpServletRequest) request)
                .getFile("file");
        String action = request.getParameter("action");
        ReturnValue<Object> responseEntity =  new ReturnValue<>();

        try{
            if(multipartFile!=null){
                byte[] bytes = multipartFile.getBytes();
                String originalName = multipartFile.getOriginalFilename();

                ImageSource img = new ImageSource();
                BufferedImage sourceImg = ImageIO.read(multipartFile.getInputStream());

                Float length = new Float(multipartFile.getSize() / 1024.0); // 源图大小

                img.setCategoryId(0);
                img.setWidth(sourceImg.getWidth());// 源图宽度
                img.setHeight(sourceImg.getHeight());// 源图高度
                img.setFileLength(length);
                img.setOriginalName(originalName);

                fileList.add(bytes);
                imgList.add(img);

            }


            FileRequest fileRequest = new FileRequest();
            fileRequest.setFiles(fileList);
            fileRequest.setImages(imgList);
            fileRequest.setUserMetaData(new HashMap<>());

            List<ImageResponse> responseList = commonService.uploadImage(fileRequest);

            if(responseList != null) {
                ImageResponse response =responseList.get(0);
                //非编辑器模式，上传的图片
                if(!StringUtils.isEmpty(action))
                {
                    response.setUrl(fctConfig.thumbnail(response.getUrl(),200));
                }

                responseEntity.setData(response);
            }

        }catch (IOException e){

            return new ReturnValue<>(404, "上传失败");
        }

        return responseEntity;
    }
}
