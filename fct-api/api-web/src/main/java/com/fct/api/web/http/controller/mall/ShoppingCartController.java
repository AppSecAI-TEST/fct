package com.fct.api.web.http.controller.mall;

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

import java.util.List;

/**
 * Created by z on 17-6-23.
 */
@RestController
@RequestMapping(value = "carts")
public class ShoppingCartController extends BaseController {

    @Autowired
    MallService mallService;

    /**获取购物车列表
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ReturnValue<List<ShoppingCart>> findCartProducts() {

        MemberLogin member = this.memberAuth();

        List<ShoppingCart> shoppingCarts = mallService.findShoppingCart(member.getMemberId(),0);
        ReturnValue<List<ShoppingCart>> response = new ReturnValue<>();
        response.setData(shoppingCarts);

        return response;
    }

    /**添加到购物车
     *
     * @param product_id
     * @param spec_id
     * @param buy_number
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ReturnValue saveCartProduct(Integer product_id, Integer spec_id, Integer buy_number) {

        product_id = ConvertUtils.toInteger(product_id);
        spec_id = ConvertUtils.toInteger(spec_id);
        buy_number = ConvertUtils.toInteger(buy_number);

        MemberLogin member = this.memberAuth();
        mallService.saveShoppingCart(member.getMemberId(), 0, product_id, spec_id, buy_number);
        return new ReturnValue(200, "添加成功");

    }

    /**从购物车删除产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public ReturnValue deleteCartProduct(@PathVariable("id") Integer id) {

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
