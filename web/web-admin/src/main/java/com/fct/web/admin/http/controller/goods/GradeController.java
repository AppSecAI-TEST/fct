package com.fct.web.admin.http.controller.goods;

import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.common.utils.StringHelper;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
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
import java.util.List;

/**
 * Created by jon on 2017/6/3.
 */
@Controller
@RequestMapping(value = "/goods/grade")
public class GradeController extends BaseController {

    @Autowired
    private MallService mallService;

    /**
     * 获取商品品级
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {

        List<GoodsGrade> lsGrade = mallService.findGoodsGrade();
        model.addAttribute("lsGrade", lsGrade);
        return "goods/grade/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required = false) Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        GoodsGrade grade =null;
        if(id>0) {
            grade = mallService.getGoodsGrade(id);
        }
        if (grade == null) {
            grade = new GoodsGrade();
            grade.setId(0);
        }
        model.addAttribute("goodsGrade", grade);
        return "goods/grade/create";
    }

    @RequestMapping(value="/save", method=RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,Integer sortindex,String img)
    {
        id = ConvertUtils.toInteger(id);
        name = ConvertUtils.toString(name);
        sortindex =ConvertUtils.toInteger(sortindex);
        img = ConvertUtils.toString(img);

        GoodsGrade grade =  null;
        if(id>0) {
            grade = mallService.getGoodsGrade(id);
        }
        if (grade == null) {
            grade = new GoodsGrade();
        }
        grade.setImg(img);
        grade.setName(name);
        grade.setSortIndex(sortindex);

        try {
            mallService.saveGoodsGrade(grade);
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

        return AjaxUtil.reload("保存宝贝品级成功");
    }
}
