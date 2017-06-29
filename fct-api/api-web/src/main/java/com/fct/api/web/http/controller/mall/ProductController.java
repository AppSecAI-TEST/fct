package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.artist.interfaces.ArtistService;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*/

/**
 * Created by z on 17-6-23.
 */

@RestController
@RequestMapping(value = "/products")
public class ProductController extends BaseController {

    @Autowired
    private MallService mallService;

    /**获取产品详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Goods> getProduct(@PathVariable("id") Integer id) {

        id= ConvertUtils.toInteger(id);

        Goods goods = mallService.getGoods(id);
/*
        Map<String, Object> product = new HashMap<>();
        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(product);
*/
        ReturnValue<Goods> response = new ReturnValue<>();
        response.setData(goods);

        return  response;
    }
}
