package com.fct.api.web.http.controller.mall;

import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.ShoppingCart;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-6-23.
 */
@RestController
@RequestMapping(value = "cart")
public class ShoppingCartController {

    @Autowired
    MallService mallService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ReturnValue<List<ShoppingCart>> index() {

        List<ShoppingCart> shoppingCarts = mallService.findShoppingCart(0,0);
        ReturnValue<List<ShoppingCart>> response = new ReturnValue<>();
        response.setData(shoppingCarts);

        return response;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ReturnValue add(Integer memberId) {

        return new ReturnValue();

    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public ReturnValue remove(Integer memberId) {

        return new ReturnValue();
    }
}
