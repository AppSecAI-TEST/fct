package com.fct.web.admin.http.controller.goods;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.interfaces.MallService;
import com.fct.web.admin.http.cache.CacheGoodsManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
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

/**
 * Created by jon on 2017/6/4.
 */
@Controller("goodsCategory")
@RequestMapping(value = "/goods/category")
public class CategoryController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private CacheGoodsManager cacheGoodsManager;
    /**
     * 获取商品分类
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(@RequestParam(required=false) String name,Model model) {

        name =ConvertUtils.toString(name);
        List<GoodsCategory> lsCategory = new ArrayList<>();
        try {
            lsCategory = mallService.findGoodsCategory(-1, name,"");
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
        id = ConvertUtils.toInteger(id);
        GoodsCategory category =null;
        if(id>0) {
            category = mallService.getGoodsCategory(id);
        }
        if (category == null) {
            category = new GoodsCategory();
            category.setId(0);
        }

        List<GoodsCategory> lsCategory = cacheGoodsManager.findGoodsCategoryByParent();

        model.addAttribute("parentCate", lsCategory);
        model.addAttribute("category", category);
        return "goods/category/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,Integer sortindex,Integer parentId,String img,String description)
    {
        id = ConvertUtils.toInteger(id);
        name =ConvertUtils.toString(name);
        sortindex = ConvertUtils.toInteger(sortindex);
        parentId = ConvertUtils.toInteger(parentId);
        img = ConvertUtils.toString(img);
        description = ConvertUtils.toString(description);

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
        category.setDescription(description);

        try {
            mallService.saveGoodsCategory(category);
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

        return AjaxUtil.goUrl("/goods/category","保存宝贝分类成功");
    }

    @RequestMapping(value="/select", method=RequestMethod.GET,produces="text/html;charset=UTF-8")
    @ResponseBody
    public String select(@RequestParam(required=false) Integer parentid,
                               @RequestParam(required=false) String subid)
    {
        parentid = ConvertUtils.toInteger(parentid);

        List<GoodsCategory> lsCate = cacheGoodsManager.findGoodsCategoryByParentId(parentid);

        StringBuilder sb = new StringBuilder();
        //sb.append("<select class=\"form-control selCate\" name=\"subid\">");
        sb.append("<option value=\"\">请选择</option>");
        for (GoodsCategory cate:lsCate
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

    @RequestMapping(value="/del", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String delete(Integer id)
    {
        id = ConvertUtils.toInteger(id);
        try {
            mallService.deleteGoodsCategory(id);
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
