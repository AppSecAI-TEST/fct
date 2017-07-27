package com.fct.web.admin.http.controller.source.video;

import com.fct.common.data.entity.ImageSource;
import com.fct.common.data.entity.VideoCategory;
import com.fct.common.data.entity.VideoSource;
import com.fct.common.interfaces.CommonService;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;

@Controller("video")
@RequestMapping(value = "/source/video")
public class VideoController extends BaseController {

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        VideoCategory category =null;
        try {
            if (id > 0) {
                category = commonService.getVideoCategory(id);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if (category == null) {
            category = new VideoCategory();
            category.setId(0);
        }
        model.addAttribute("category", category);
        return "source/video/category/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(MultipartFile file,String url,String id, String name, String img, Integer categoryid, Integer sortindex)
    {
        id = ConvertUtils.toString(id);
        name = ConvertUtils.toString(name);
        sortindex =ConvertUtils.toInteger(sortindex);
        img = ConvertUtils.toString(img);
        url = ConvertUtils.toString(url);

        VideoSource videoSource =  null;
        if(!StringUtils.isEmpty(id)) {
            videoSource = commonService.getVideoSource(id);
        }
        if (videoSource == null) {
            videoSource = new VideoSource();
        }
        videoSource.setName(name);
        videoSource.setSortIndex(sortindex);
        videoSource.setImg(img);

        try {

            byte[] bytes = null;
            if(!url.equals(videoSource.getUrl()))
            {
                bytes = file.getBytes();
                String originalName = file.getOriginalFilename();


                Float length = new Float(file.getSize() / 1024.0); // 源图大小

                videoSource.setFileSize(length);
            }

            commonService.uploadVideo(videoSource,bytes);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.remind(exp.getMessage());
        }
        catch (Exception exp)
        {
            //这里没有写进文件
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.remind("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("保存视频分类成功");
    }
}
