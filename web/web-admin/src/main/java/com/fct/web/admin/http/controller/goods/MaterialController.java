package com.fct.web.admin.http.controller.goods;

import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.PageUtil;
import com.fct.common.utils.StringHelper;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jon on 2017/6/6.
 */
@Controller
@RequestMapping(value = "/goods/material")
public class MaterialController {

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(HttpServletRequest request,Model model) {
        String q = StringHelper.toString(request.getParameter("q"));
        Integer status = StringHelper.toInteger(request.getParameter("status"),-1);
        Integer pageIndex = StringHelper.toPageIndex(request.getParameter("page"));
        Integer pageSize = 4;

        PageResponse<GoodsMaterial> pageResponse = mallService.findMaterial(0,q,status,pageIndex,pageSize);

        model.addAttribute("pageHtml",PageUtil.getPager(pageResponse.getTotalCount(),pageIndex,
                pageSize,"?page=%d"));

        model.addAttribute("lsMaterial", pageResponse.getElements());
        return "goods/material/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) Integer id, Model model) {
        GoodsGrade grade =null;
        if(id>0) {
            grade = mallService.getGoodsGrade(id);
        }
        if (grade == null) {
            grade = new GoodsGrade();
            grade.setId(0);
        }
        model.addAttribute("goodsGrade", grade);
        return "goods/material/create";
    }

    public ModelAndView upstatus(@RequestParam Integer id)
    {
        if(id ==null || id<=0)
        {
            return AjaxUtil.alert("id不正确。");
        }
        try
        {
            mallService.updateMaterialStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.remind("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("更新审核状态成功。");
    }
}
