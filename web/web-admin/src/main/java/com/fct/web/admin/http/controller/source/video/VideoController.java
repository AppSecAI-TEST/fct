package com.fct.web.admin.http.controller.source.video;

import com.alibaba.dubbo.common.URL;
import com.fct.common.data.entity.VideoCategory;
import com.fct.common.data.entity.VideoSource;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.PageResponse;
import com.fct.common.interfaces.VideoResponse;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.web.admin.http.cache.CacheCommonManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("video")
@RequestMapping(value = "/source/video")
public class VideoController extends BaseController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private CacheCommonManager cacheCommonManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String name,Integer categoryid,Integer status,String starttime,String endtime,
                        Integer page, Model model) {

        name = ConvertUtils.toString(name);
        status =ConvertUtils.toInteger(status,-1);
        categoryid = ConvertUtils.toInteger(categoryid);
        page =ConvertUtils.toPageIndex(page);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);

        List<VideoCategory> lsCategory = cacheCommonManager.findCacheVideoCategory();//这样引用出错

        Integer pageSize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");
        if(!StringUtils.isEmpty(name))
        {
            sb.append("&q="+ URL.encode(name));
        }
        if(categoryid>0)
        {
            sb.append("&categoryid="+categoryid);
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+ starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+ endtime);
        }
        PageResponse<VideoSource> pageResponse = null;

        try {

            pageResponse = commonService.findVideoSource(name,categoryid,status,"",starttime,endtime,
                    page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<VideoSource>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("name", name);
        query.put("status", status);
        query.put("starttime", starttime);
        query.put("endtime", endtime);
        query.put("lsCategory", lsCategory);
        query.put("categoryid",categoryid);

        model.addAttribute("query", query);
        model.addAttribute("lsVideo", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,sb.toString()));
        model.addAttribute("cache", cacheCommonManager);

        return "source/video/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) String id, Model model) {
        id = ConvertUtils.toString(id);
        VideoSource videoSource =null;
        String response = "";
        try {
            if (!StringUtils.isEmpty(id)) {
                videoSource = commonService.getVideoSource(id);

                VideoResponse videoResponse = new VideoResponse();
                videoResponse.setFileSize(videoSource.getFileSize());
                videoResponse.setFileId(videoSource.getFileId());
                videoResponse.setFileType(videoSource.getFileType());
                videoResponse.setUrl(videoResponse.getUrl());
                videoResponse.setOriginalName(videoResponse.getOriginalName());

                response = JsonConverter.toJson(videoResponse);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if (videoSource == null) {
            videoSource = new VideoSource();
        }
        model.addAttribute("video", videoSource);
        model.addAttribute("videoResponse", response);
        model.addAttribute("cache", cacheCommonManager);
        return "source/video/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(String videoresponse,String id, String name, String img, Integer categoryid,
                       String intro, Integer sortindex)
    {
        id = ConvertUtils.toString(id);
        name = ConvertUtils.toString(name);
        sortindex =ConvertUtils.toInteger(sortindex);
        img = ConvertUtils.toString(img);
        categoryid = ConvertUtils.toInteger(categoryid);
        intro = ConvertUtils.toString(intro);
        videoresponse = ConvertUtils.toString(videoresponse);

        VideoSource videoSource =  null;
        if(!StringUtils.isEmpty(id)) {
            videoSource = commonService.getVideoSource(id);
        }
        if (videoSource == null) {
            videoSource = new VideoSource();
        }

        try {

            VideoResponse response = JsonConverter.toObject(videoresponse,VideoResponse.class);
            videoSource.setFileType(response.getFileType());
            videoSource.setFileId(response.getFileId());
            videoSource.setUrl(response.getUrl());
            videoSource.setFileSize(response.getFileSize());
            videoSource.setOriginalName(response.getOriginalName());

            videoSource.setName(name);
            videoSource.setSortIndex(sortindex);
            videoSource.setImg(img);
            videoSource.setCategoryId(categoryid);
            videoSource.setIntro(intro);

            commonService.saveVideoSource(videoSource);
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

        return AjaxUtil.reload("保存视频成功");
    }

    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(String id,String action)
    {
        id = ConvertUtils.toString(id);
        action = ConvertUtils.toString(action);

        try {

            switch (action)
            {
                case "audi":
                    commonService.updateVideoSourceStatus(id);
                    break;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            //这里没有写进文件
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("处理成功。");
    }
}
