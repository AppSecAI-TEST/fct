package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.json.JsonResponseEntity;
import com.fct.api.web.http.model.Home;
import com.fct.core.utils.ConvertUtils;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-6-23.
 */
@RestController
@RequestMapping(value = "/")
public class MallController {

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public JsonResponseEntity<Home> index(String categoryId, Integer levelId, Integer pageIndex, Integer pageSize) {

        categoryId = ConvertUtils.toString(categoryId);
        levelId = ConvertUtils.toInteger(levelId);
        pageIndex = ConvertUtils.toPageIndex(pageIndex);
        pageSize = ConvertUtils.toInteger(pageSize);
        pageSize = pageSize > 0 ? pageSize : 20;

        List<GoodsCategory> categories = mallService.findGoodsCategory(0, "", "");
        PageResponse<Goods> goodsList = mallService.findGoods("", categoryId, levelId, 0,
                0,0,0,1,pageIndex, pageSize);

        Home home = new Home();
        home.categoryList = categories;
        home.goodsList = goodsList;

        JsonResponseEntity<Home> response = new JsonResponseEntity<>();
        response.setData(home);

        return  response;
    }
}
