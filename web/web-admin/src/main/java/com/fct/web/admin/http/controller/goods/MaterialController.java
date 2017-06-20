package com.fct.web.admin.http.controller.goods;

import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.common.utils.StringHelper;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jon on 2017/6/6.
 */
@Controller
@RequestMapping(value = "/goods/material")
public class MaterialController extends BaseController {

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q,Integer status,Integer page,Model model) {
        q = ConvertUtils.toString(q);
        status = ConvertUtils.toInteger(status,-1);
        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        PageResponse<GoodsMaterial> pageResponse =null;

        try {
            pageResponse = mallService.findMaterial(0, q, -1, status, page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(pageResponse == null)
            pageResponse = new PageResponse<GoodsMaterial>();

        model.addAttribute("pageHtml",PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,"?page=%d"));

        model.addAttribute("lsMaterial", pageResponse.getElements());
        return "goods/material/index";
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public String select(String ids,Model model) {

        ids = ConvertUtils.toString(ids);
        model.addAttribute("materialIds", ids);
        return "goods/material/select";
    }
    @ResponseBody
    @RequestMapping(value = "/ajaxload", method = RequestMethod.GET,produces="text/html;charset=UTF-8")
    public String ajaxLoad(String q,String ids)
    {
        q = ConvertUtils.toString(q);
        ids = ConvertUtils.toString(ids);

        PageResponse<GoodsMaterial> pageResponse = mallService.findMaterial(0,q,0,1,1,20);

        StringBuilder sb = new StringBuilder();

        String[] arrId = ids.split(",");
        List<String> idList = Arrays.asList(arrId);

        for (GoodsMaterial m:pageResponse.getElements()
             ) {
            String checked = "";
            if(idList.contains(m.getId().toString()))
            {
                checked = " checked=\"checked\"";
            }
            String json = "{id:'"+ m.getId()+"',name:'"+ m.getName() +"'}";

            sb.append("<tr>");
            sb.append("<td>"+ m.getName() +"</td>");
            sb.append("<td>");
            sb.append("<input type=\"checkbox\" class=\"checkMaterial\" value=\""+ m.getId() +"\" data-json=\""+ json +"\" name=\"materialId\" "+checked+"/>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        GoodsMaterial material =null;
        if(id>0) {
            material = mallService.getMaterial(id);
        }
        if (material == null) {
            material = new GoodsMaterial();
            material.setId(0);
        }
        model.addAttribute("material", material);
        return "goods/material/create";
    }

    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(@RequestParam Integer id)
    {
        id = ConvertUtils.toInteger(id);
        if(id<=0)
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
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("更新审核状态成功。");
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,String desc,String image)
    {
        id = ConvertUtils.toInteger(id);
        name =ConvertUtils.toString(name);
        desc = ConvertUtils.toString(desc);
        image = ConvertUtils.toString(image);

        GoodsMaterial material =  null;
        if(id>0) {
            material = mallService.getMaterial(id);
        }
        if (material == null) {
            material = new GoodsMaterial();
        }
        material.setTypeid(0);
        material.setImages(image);
        material.setName(name);
        material.setDescription(desc);

        try {
            mallService.saveMaterial(material);
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

        return AjaxUtil.goUrl("/goods/material","保存泥料信息成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String delete(Integer id)
    {
        id = ConvertUtils.toInteger(id);
        if(id<=0)
        {
            return AjaxUtil.alert("id不正确。");
        }
        try
        {
            mallService.deleteGoodsMaterial(id);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("删除泥料成功。");
    }
}
