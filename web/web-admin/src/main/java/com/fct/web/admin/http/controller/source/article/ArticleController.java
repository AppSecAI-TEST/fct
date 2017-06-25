package com.fct.web.admin.http.controller.source.article;

import com.alibaba.dubbo.common.URL;
import com.fct.common.data.entity.Article;
import com.fct.common.data.entity.ArticleCategory;
import com.fct.core.exceptions.Exceptions;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.PageResponse;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.web.admin.http.cache.CacheCommonManager;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/source/article")
public class ArticleController extends BaseController{

    @Autowired
    private CommonService commonService;

    @Autowired
    private CacheCommonManager cacheCommonManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String title,String catecode,Integer status,String starttime,String endtime,
            Integer page, Model model) {

        catecode = ConvertUtils.toString(catecode);
        title = ConvertUtils.toString(title);
        status =ConvertUtils.toInteger(status,-1);
        page =ConvertUtils.toPageIndex(page);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);

        List<ArticleCategory> lsCategory = cacheCommonManager.findArticleCategoryByParent();//这样引用出错

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(title))
        {
            pageUrl +="&q="+ URL.encode(title);
        }
        if(!StringUtils.isEmpty(catecode))
        {
            pageUrl +="&catecode="+ catecode;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl +="&starttime="+ starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl +="&endtime="+ endtime;
        }
        if(status>-1)
        {
            pageUrl +="&status="+status;
        }
        PageResponse<Article> pageResponse = null;

        try {

            pageResponse = commonService.findArticle(title,catecode,status,starttime,endtime,
                    page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Article>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("title", title);
        query.put("status", status);
        query.put("starttime", starttime);
        query.put("endtime", endtime);
        query.put("parentCate", lsCategory);
        query.put("catecode",catecode);

        model.addAttribute("query", query);
        model.addAttribute("lsArticle", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));
        model.addAttribute("cache", cacheCommonManager);

        return "source/article/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required=false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        Article article =null;
        if(id>0) {
            article = commonService.getArticle(id);
        }
        if (article == null) {
            article = new Article();
            article.setId(0);
            article.setCategoryCode("");
        }

        List<ArticleCategory> lsCategory = cacheCommonManager.findArticleCategoryByParent();

        model.addAttribute("lsCategory", lsCategory);
        model.addAttribute("article", article);
        return "source/article/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String categorycode,String title,String intro,String content,Integer sortindex,
                       String banner,Integer status,String source)
    {
        id = ConvertUtils.toInteger(id);
        categorycode = ConvertUtils.toString(categorycode);
        title = ConvertUtils.toString(title);
        intro = ConvertUtils.toString(intro);
        content = ConvertUtils.toString(content);
        sortindex =ConvertUtils.toInteger(sortindex);
        banner = ConvertUtils.toString(banner);
        status = ConvertUtils.toInteger(status);
        source = ConvertUtils.toString(source);

        Article article = null;
        if(id>0) {
            article = commonService.getArticle(id);
        }
        if (article == null) {
            article = new Article();
        }

        article.setTitle(title);
        article.setCategoryCode(categorycode);
        article.setIntro(intro);
        article.setContent(content);
        article.setSortIndex(sortindex);
        article.setStatus(status);
        article.setBanner(banner);
        article.setSource(source);

        try {
            commonService.saveArticle(article);
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

        return AjaxUtil.goUrl("/source/article","保存文章成功");
    }
}
