package com.fct.web.admin.http.controller.source.image;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.common.data.entity.ImageCategory;
import com.fct.common.interfaces.CommonService;
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

@Controller("imageCategory")
@RequestMapping(value = "/source/image/category")
public class CategoryController  extends BaseController{

    @Autowired
    private CommonService commonService;

    /**
     * 获取图片分类
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {

        List<ImageCategory> lsCate = null;
        try {
            lsCate = commonService.findImageCategory();
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(lsCate == null)
            lsCate = new ArrayList<>();

        model.addAttribute("lsCategory", lsCate);
        return "source/image/category/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        ImageCategory category =null;
        try {
            if (id > 0) {
                category = commonService.getImageCategory(id);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if (category == null) {
            category = new ImageCategory();
            category.setId(0);
        }
        model.addAttribute("category", category);
        return "source/image/category/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,Integer sortindex)
    {
        id = ConvertUtils.toInteger(id);
        name = ConvertUtils.toString(name);
        sortindex =ConvertUtils.toInteger(sortindex);

        ImageCategory category =  null;
        if(id>0) {
            category = commonService.getImageCategory(id);
        }
        if (category == null) {
            category = new ImageCategory();
        }
        category.setName(name);
        category.setSortIndex(sortindex);

        try {
            commonService.saveImageCategory(category);
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

        return AjaxUtil.reload("保存图片分类成功");
    }
}
