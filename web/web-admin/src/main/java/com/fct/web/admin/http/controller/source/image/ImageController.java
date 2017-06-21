package com.fct.web.admin.http.controller.source.image;

import com.alibaba.dubbo.common.URL;
import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.source.data.entity.ImageCategory;
import com.fct.source.data.entity.ImageSource;
import com.fct.source.interfaces.PageResponse;
import com.fct.source.interfaces.SourceService;
import com.fct.web.admin.http.cache.CacheSourceManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/source/image")
public class ImageController extends BaseController{

    @Autowired
    private SourceService sourceService;

    @Autowired
    private CacheSourceManager cacheSourceManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String name,Integer categoryid,Integer status,String starttime,String endtime,
                        Integer page, Model model) {

        name = ConvertUtils.toString(name);
        status =ConvertUtils.toInteger(status,-1);
        categoryid = ConvertUtils.toInteger(categoryid);
        page =ConvertUtils.toPageIndex(page);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);

        List<ImageCategory> lsCategory = cacheSourceManager.findImageCategory();//这样引用出错

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(name))
        {
            pageUrl +="&q="+ URL.encode(name);
        }
        if(categoryid>0)
        {
            pageUrl +="&categoryid="+categoryid;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl +="&starttime="+ starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl +="&endtime="+ endtime;
        }
        PageResponse<ImageSource> pageResponse = null;

        try {

            pageResponse = sourceService.findImageSource(name,categoryid,status,"",starttime,endtime,
                    page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<ImageSource>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("name", name);
        query.put("status", status);
        query.put("starttime", starttime);
        query.put("endtime", endtime);
        query.put("lsCategory", lsCategory);
        query.put("categoryid",categoryid);

        model.addAttribute("query", query);
        model.addAttribute("lsImage", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));
        model.addAttribute("cache", cacheSourceManager);

        return "source/image/index";
    }
}
