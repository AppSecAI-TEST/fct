package com.fct.web.admin.http.controller.artist;

import com.alibaba.dubbo.common.URL;
import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.interfaces.ArtistService;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller("artistComment")
@RequestMapping(value = "/artist/comment")
public class CommentController extends BaseController {

    @Autowired
    private ArtistService artistService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String name,Integer status,Integer artistid,Integer page, Model model) {

        name = ConvertUtils.toString(name);
        status =ConvertUtils.toInteger(status,-1);
        artistid = ConvertUtils.toInteger(artistid);
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
        if(artistid>0)
        {
            pageUrl+="&artistid="+artistid;
        }
        PageResponse<ArtistComment> pageResponse = null;

        try {

            pageResponse = artistService.findArtistComment(artistid,0,name,status,0,
                    page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<ArtistComment>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("name", name);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsComment", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "artist/comment";
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
                    artistService.updateArtistCommentStatus(id);
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
