package com.fct.web.admin.http.controller.artist;

import com.alibaba.dubbo.common.URL;
import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.web.admin.http.cache.CacheArtistManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
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
import java.util.Map;

@Controller("artistDynamic")
@RequestMapping(value = "/artist/dynamic")
public class DynamicController extends BaseController{

    @Autowired
    private ArtistService artistService;

    @Autowired
    private CacheArtistManager cacheArtistManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer artistid, Integer status, String q,String starttime,String endtime,
                        Integer page, Model model) {

        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        artistid = ConvertUtils.toInteger(artistid);
        status =ConvertUtils.toInteger(status,-1);
        q =  ConvertUtils.toString(q);
        page =ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");
        if(!StringUtils.isEmpty(q))
        {
            sb.append("&q="+ URL.encode(q));
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+ starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+ endtime);
        }
        if(status>-1)
        {
            sb.append("&status="+status);
        }
        if(artistid>0)
        {
            sb.append("&artistid="+artistid);
        }
        PageResponse<ArtistDynamic> pageResponse = null;

        try {

            pageResponse = artistService.findArtistDynamic(artistid,q,status,starttime,
                    endtime,page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<ArtistDynamic>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("starttime", starttime);
        query.put("endtime", endtime);
        query.put("status", status);
        query.put("q", q);

        model.addAttribute("query", query);
        model.addAttribute("cache", cacheArtistManager);
        model.addAttribute("lsDynamic", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,sb.toString()));

        return "artist/dynamic/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required=false) Integer id, Integer artistid,Model model) {
        id = ConvertUtils.toInteger(id);
        artistid = ConvertUtils.toInteger(artistid);
        ArtistDynamic dynamic =null;
        if(id>0) {
            dynamic = artistService.getArtistDynamic(id);
        }
        if (dynamic == null) {
            dynamic = new ArtistDynamic();
            dynamic.setId(0);
            dynamic.setArtistId(artistid);
        }

        model.addAttribute("dynamic", dynamic);
        model.addAttribute("cache", cacheArtistManager);
        return "artist/dynamic/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,Integer artistid,String content,String images,String videourl,String videoid,
                       String videoimg, Integer status)
    {
        id = ConvertUtils.toInteger(id);
        status = ConvertUtils.toInteger(status);
        artistid = ConvertUtils.toInteger(artistid);
        content =ConvertUtils.toString(content);
        images = ConvertUtils.toString(images);
        videourl = ConvertUtils.toString(videourl);
        videoid = ConvertUtils.toString(videoid);
        videoimg = ConvertUtils.toString(videoimg);

        ArtistDynamic dynamic = null;
        if(id>0) {
            dynamic = artistService.getArtistDynamic(id);
        }
        if (dynamic == null) {
            dynamic = new ArtistDynamic();
            dynamic.setArtistId(id);
        }

        dynamic.setArtistId(artistid);
        dynamic.setContent(content);
        dynamic.setImages(images);
        dynamic.setIsTop(0);
        dynamic.setVideoId(videoid);
        dynamic.setVideoUrl(videourl);
        dynamic.setVideoImg(videoimg);
        dynamic.setStatus(status);

        try {
            artistService.saveArtistDynamic(dynamic);
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

        return AjaxUtil.goUrl("/artist/dynamic","保存艺人动态成功");
    }

    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(Integer id,String action)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);

        try {

            switch (action)
            {
                case "audi":
                    artistService.updateArtistDynamicStatus(id);
                    break;
                case "top":
                    artistService.setArtistDynamicTop(id);
                    break;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp) {
            //这里没有写进文件
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("处理成功。");
    }
}
