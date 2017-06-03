package com.fct.web.admin.http.controller;

import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/**
 * Created by jon on 2017/6/1.
 */

@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private MallService mallService;

    /**
     * 获取商品品级
     * @return
     */
    @RequestMapping(value = "/grade", method = RequestMethod.GET)
    public String grade(Model model) {

        List<GoodsGrade> lsGrade = mallService.findGoodsGrade();
        model.addAttribute("lsGrade", lsGrade);
        return "goods/grade";
    }
}
