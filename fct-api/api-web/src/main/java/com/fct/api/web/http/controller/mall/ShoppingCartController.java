package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.ShoppingCart;
import com.fct.mall.interfaces.MallService;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-23.
 */
@RestController
@RequestMapping(value = "/mall/carts")
public class ShoppingCartController extends BaseController {

    @Autowired
    MallService mallService;

    @Autowired
    private ProductCache productCache;

    /**获取购物车列表
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> findCartProducts() {

        MemberLogin member = this.memberAuth();

        Map<String, Object> result = new HashMap<>();

        List<ShoppingCart> lsCart = mallService.findShoppingCart(member.getMemberId(),0);
        List<Map<String, Object>> lsMap = new ArrayList<>();
        String productIds = "";
        if (lsCart != null) {
            for (ShoppingCart cart:lsCart) {

                productIds += (productIds != "" ? "," : "") + cart.getGoodsId();

                Map<String, Object> map = new HashMap<>();
                map.put("id", cart.getId());
                map.put("goodsId", cart.getGoodsId());
                map.put("name", cart.getGoods().getName());
                map.put("img", fctResourceUrl.thumbSmall(cart.getGoods().getImg()));
                map.put("promotionPrice", cart.getGoods().getPromotionPrice());
                map.put("price", cart.getGoods().getPrice());
                map.put("specId", cart.getGoodsSpecId());
                map.put("specName", cart.getGoods().getSpecName());
                map.put("stockCount", cart.getGoods().getBuyCount());
                map.put("buyCount", cart.getBuyCount());
                lsMap.add(map);
            }
        }

        result.put("cartList", lsMap);
        result.put("likeList", productCache.guessProducts(productIds, 4));

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(result);

        return response;
    }

    /**添加到购物车
     *
     * @param product_id
     * @param spec_id
     * @param buy_number
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue<Integer> saveCartProduct(Integer product_id, Integer spec_id, Integer buy_number) {

        product_id = ConvertUtils.toInteger(product_id);
        spec_id = ConvertUtils.toInteger(spec_id);
        buy_number = ConvertUtils.toInteger(buy_number);
        if (product_id < 1)
            return new ReturnValue<>(404, "产品有误");

        if (buy_number < 1)
            return new ReturnValue<>(404, "购买数量不能小于1");

        MemberLogin member = this.memberAuth();
        mallService.saveShoppingCart(member.getMemberId(), 0, product_id, spec_id, buy_number);


        ReturnValue<Integer> response = new ReturnValue<>();
        //返回用户当前购物车数量
        response.setData(mallService.getShoopingCartCount(member.getMemberId()));

        return response;

    }

    /**从购物车删除产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public ReturnValue deleteCartProduct(@PathVariable("id") Integer id) {

        if (id < 1)
            return new ReturnValue(404, "购物车记录不存在");

        MemberLogin member = this.memberAuth();
        mallService.deleteShoppingCart(member.getMemberId(), 0, id);

        return new ReturnValue(200, "删除成功");
    }

    @Deprecated
    @RequestMapping(value = "clean", method = RequestMethod.POST)
    public ReturnValue cleanCartProducts() {

        return new ReturnValue(200, "删除成功");
    }
}
