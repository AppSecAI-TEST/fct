package com.fct.web.admin.http.controller.artist;

import com.fct.artist.data.entity.ArtistLive;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.web.admin.http.cache.CacheArtistManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller("artistLive")
@RequestMapping(value = "/artist/live")
public class LiveController extends BaseController{

    @Autowired
    private ArtistService artistService;

    @Autowired
    private CacheArtistManager cacheArtistManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer artistid, Integer status, String startitme,String endtime,
                        Integer page, Model model) {

        startitme = ConvertUtils.toString(startitme);
        endtime = ConvertUtils.toString(endtime);
        artistid = ConvertUtils.toInteger(artistid);
        status =ConvertUtils.toInteger(status,-1);
        page =ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(startitme))
        {
            pageUrl +="&startitme="+ startitme;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl +="&endtime="+ endtime;
        }
        if(status>-1)
        {
            pageUrl +="&status="+status;
        }
        PageResponse<ArtistLive> pageResponse = null;

        try {

            pageResponse = artistService.findArtistLive(artistid,status,startitme,endtime,page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<ArtistLive>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("starttime", startitme);
        query.put("endtime", endtime);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsLive", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "artist/live/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Integer artistid, Model model) {
        artistid = ConvertUtils.toInteger(artistid);
        ArtistLive live =null;
        if(artistid>0) {
            live = artistService.getArtistLive(artistid);
        }
        if (live == null) {
            live = new ArtistLive();
            live.setId(0);
            live.setArtistId(artistid);
        }

        model.addAttribute("cache", cacheArtistManager);
        model.addAttribute("live", live);
        return "artist/live/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String title,String banner,String liveid, Integer status,
                       String starttime,String endtime)
    {
        id = ConvertUtils.toInteger(id);
        title = ConvertUtils.toString(title);
        banner = ConvertUtils.toString(banner);
        status = ConvertUtils.toInteger(status);
        liveid =ConvertUtils.toString(liveid);
        Date startTime =ConvertUtils.toDate(starttime);
        Date endTime = ConvertUtils.toDate(endtime);

        ArtistLive live = null;
        if(id>0) {
            live = artistService.getArtistLive(id);
        }
        if (live == null) {
            live = new ArtistLive();
            live.setArtistId(id);
        }

        live.setTitle(title);
        live.setStatus(status);
        live.setBanner(banner);
        live.setLiveId(liveid);
        live.setStartTime(startTime);
        live.setEndTime(endTime);

        try {
            artistService.saveArtistLive(live);
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

        return AjaxUtil.goUrl("/artist/live","保存艺人直播成功");
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
                    artistService.updateArtistLiveStatus(id);
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
