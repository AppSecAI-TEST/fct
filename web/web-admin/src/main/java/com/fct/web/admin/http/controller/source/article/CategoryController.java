package com.fct.web.admin.http.controller.source.article;

import com.fct.source.data.entity.ArticleCategory;
import com.fct.common.exceptions.Exceptions;
import com.fct.source.interfaces.SourceService;
import com.fct.common.utils.ConvertUtils;
import com.fct.web.admin.http.cache.CacheSourceManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/source/article/category")
@Controller("articleCategory")
public class CategoryController extends BaseController {

    @Autowired
    private SourceService sourceService;

    @Autowired
    private CacheSourceManager cacheSourceManager;
    /**
     * 获取分类
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(@RequestParam(required=false) String name, Model model) {

        name = ConvertUtils.toString(name);
        List<ArticleCategory> lsCategory = new ArrayList<>();
        try {
            lsCategory = sourceService.findArticleCategory(-1, name,"");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        model.addAttribute("lsCategory", lsCategory);
        return "source/article/category/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required=false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        ArticleCategory category =null;
        if(id>0) {
            category = sourceService.getArticleCategory(id);
        }
        if (category == null) {
            category = new ArticleCategory();
            category.setId(0);
        }

        List<ArticleCategory> lsCategory = cacheSourceManager.findArticleCategoryByParent();

        model.addAttribute("parentCate", lsCategory);
        model.addAttribute("category", category);
        return "source/article/category/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,Integer sortindex,Integer parentId)
    {
        id = ConvertUtils.toInteger(id);
        name =ConvertUtils.toString(name);
        sortindex = ConvertUtils.toInteger(sortindex);
        parentId = ConvertUtils.toInteger(parentId);

        ArticleCategory category =  null;
        if(id>0) {
            category = sourceService.getArticleCategory(id);
        }
        if (category == null) {
            category = new ArticleCategory();
        }
        category.setName(name);
        category.setParentId(parentId);
        category.setSortIndex(sortindex);

        try {
            sourceService.saveArticleCategory(category);
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

        return AjaxUtil.goUrl("/source/article/category","保存分类成功");
    }

    @RequestMapping(value="/select", method=RequestMethod.GET,produces="text/html;charset=UTF-8")
    @ResponseBody
    public String select(@RequestParam(required=false) Integer parentid,
                         @RequestParam(required=false) String subid)
    {
        parentid = ConvertUtils.toInteger(parentid);

        List<ArticleCategory> lsCate = cacheSourceManager.findArticleCategoryByParentId(parentid);

        StringBuilder sb = new StringBuilder();
        //sb.append("<select class=\"form-control selCate\" name=\"subid\">");
        sb.append("<option value=\"\">请选择</option>");
        for (ArticleCategory cate:lsCate
                ) {
            String selected = "";
            if(ConvertUtils.toString(subid).equals(cate.getCode()))
            {
                selected = " selected=\"selected\"";
            }
            sb.append("<option value=\""+ cate.getCode() +"\" "+ selected +">"+ cate.getName() +"</option>");
        }

        return sb.toString();
    }

    @RequestMapping(value="/delete", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String delete(Integer id)
    {
        id = ConvertUtils.toInteger(id);
        try {
            sourceService.deleteArticleCategory(id);
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

        return AjaxUtil.reload("删除分类成功");
    }
}
