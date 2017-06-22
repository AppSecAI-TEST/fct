package com.fct.web.admin.http.controller.artist;

import com.alibaba.dubbo.common.URL;
import com.fct.artist.data.entity.Artist;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/artist")
public class ArtistController extends BaseController{

    @Autowired
    private ArtistService artistService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String name,Integer status,Integer page, Model model) {

        name = ConvertUtils.toString(name);
        status =ConvertUtils.toInteger(status,-1);
        page =ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(name))
        {
            pageUrl +="&name="+ URL.encode(name);
        }
        if(status>-1)
        {
            pageUrl +="&status="+status;
        }
        PageResponse<Artist> pageResponse = null;

        try {

            pageResponse = artistService.findArtist(name,status,page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Artist>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("name", name);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsArtist", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "artist/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required=false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        Artist artist =null;
        if(id>0) {
            artist = artistService.getArtist(id);
        }
        if (artist == null) {
            artist = new Artist();
            artist.setId(0);
        }

        model.addAttribute("artist", artist);
        return "artist/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,String title,String intro,String content,Integer sortindex,
                       String banner,String mainimg,String headportrait, Integer status,Integer viewcount)
    {
        id = ConvertUtils.toInteger(id);
        name = ConvertUtils.toString(name);
        title = ConvertUtils.toString(title);
        intro = ConvertUtils.toString(intro);
        content = ConvertUtils.toString(content);
        sortindex =ConvertUtils.toInteger(sortindex);
        banner = ConvertUtils.toString(banner);
        status = ConvertUtils.toInteger(status);
        mainimg = ConvertUtils.toString(mainimg);
        headportrait = ConvertUtils.toString(headportrait);
        viewcount = ConvertUtils.toInteger(viewcount);

        Artist artist = null;
        if(id>0) {
            artist = artistService.getArtist(id);
        }
        if (artist == null) {
            artist = new Artist();
        }

        artist.setTitle(title);
        artist.setName(name);
        artist.setIntro(intro);
        artist.setDescription(content);
        artist.setSortIndex(sortindex);
        artist.setStatus(status);
        artist.setBanner(banner);
        artist.setHeadPortrait(headportrait);
        artist.setMainImg(mainimg);
        artist.setViewCount(viewcount);

        try {
            artistService.saveArtist(artist);
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

        return AjaxUtil.goUrl("/artist","保存艺人成功");
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
                    artistService.updateArtistStatus(id);
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
