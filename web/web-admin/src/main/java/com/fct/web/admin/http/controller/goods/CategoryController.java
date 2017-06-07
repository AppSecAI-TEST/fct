package com.fct.web.admin.http.controller.goods;

import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.StringHelper;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import com.fct.web.admin.annotations.NoNullValue;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/6/4.
 */
@Controller
@RequestMapping(value = "/goods/category")
public class CategoryController {

    @Autowired
    private MallService mallService;
    /**
     * 获取商品分类
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(@RequestParam(required=false) String name,Model model) {

        List<GoodsCategory> lsCategory = new ArrayList<>();
        try {
            lsCategory = mallService.findGoodsCategory(-1, StringHelper.toString(name),"");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        model.addAttribute("lsCategory", lsCategory);
        return "goods/category/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required=false) Integer id, Model model) {
        GoodsCategory category =null;
        if(id>0) {
            category = mallService.getGoodsCategory(id);
        }
        if (category == null) {
            category = new GoodsCategory();
            category.setId(0);
        }

        List<GoodsCategory> lsCategory = mallService.findGoodsCategory(0, "","");
        if(lsCategory ==null && lsCategory.size() <=0) {
            lsCategory = new ArrayList<>();
        }
        model.addAttribute("parentCate", lsCategory);
        model.addAttribute("category", category);
        return "goods/category/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST)
    public ModelAndView save(HttpServletRequest request)
    {
        Integer id = StringHelper.toInteger(request.getParameter("id"),0);
        String name = StringHelper.toString(request.getParameter("name"));
        Integer sortindex = StringHelper.toInteger(request.getParameter("sortindex"),0);
        Integer parentId = StringHelper.toInteger(request.getParameter("parentid"),0);
        String img = StringHelper.toString(request.getParameter("img"));

        GoodsCategory category =  null;
        if(id>0) {
            category = mallService.getGoodsCategory(id);
        }
        if (category == null) {
            category = new GoodsCategory();
        }
        category.setImg(img);
        category.setName(name);
        category.setParentId(parentId);
        category.setSortIndex(sortindex);

        try {
            mallService.saveGoodsCategory(category);
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

        return AjaxUtil.reload("保存宝贝分类成功");
    }

    @RequestMapping(value="/select", method=RequestMethod.GET,produces="text/html;charset=UTF-8")
    @ResponseBody
    public String select(@RequestParam(required=false) String parentid,
                               @RequestParam(required=false) String subid)
    {
        Integer pid = StringHelper.toInteger(parentid,0);
        List<GoodsCategory> lsCate =null;
        if(pid >0 ) {
            lsCate = mallService.findGoodsCategory(StringHelper.toInteger(parentid,0), "", "");
        }
        if(lsCate == null)
            lsCate = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("<select class=\"form-control\" name=\"subid\">");
        sb.append("<option value=\"\">请选择</option>");
        for (GoodsCategory cate:lsCate
             ) {
            String selected = "";
            if(StringHelper.toInteger(subid,0) == cate.getId())
            {
                selected = " selected=\"selected\"";
            }
            sb.append("<option value=\""+ cate.getCode() +"\" "+ selected +">"+ cate.getName() +"</option>");
        }
        sb.append("</select>");

        //ModelAndView mav = new ModelAndView();//实例化一个VIew的ModelAndView实例

       // mav.addObject(sb.toString());//添加一个带名的model对象

       return sb.toString();
    }
}
