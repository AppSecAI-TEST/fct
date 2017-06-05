package com.fct.web.admin.http.controller;

import com.fct.common.utils.StringHelper;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import com.sun.deploy.panel.ITreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String grade(Model model) {

        List<GoodsGrade> lsGrade = mallService.findGoodsGrade();
        model.addAttribute("lsGrade", lsGrade);
        return "goods/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(required =false) Integer id, Model model) {
        if(id == null)
            id = 0;
        Goods goods =null;
        if(id>0) {
            goods = mallService.getGoods(id);
        }
        if (goods == null) {
            goods = new Goods();
            goods.setId(0);
        }
        List<GoodsCategory> lsCategory = mallService.findGoodsCategory(0, "","");
        if(lsCategory ==null && lsCategory.size() <=0) {
            lsCategory = new ArrayList<>();
        }
        model.addAttribute("parentCate", lsCategory);
        model.addAttribute("goods", goods);
        return "goods/create";
    }
}
