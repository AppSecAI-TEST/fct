package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.json.JsonResponseEntity;
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
    public JsonResponseEntity<List<ShoppingCart>> index() {

        List<ShoppingCart> shoppingCarts = mallService.findShoppingCart(0,0);
        JsonResponseEntity<List<ShoppingCart>> response = new JsonResponseEntity<>();
        response.setData(shoppingCarts);

        return response;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public JsonResponseEntity<String> add(Integer memberId) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return response;

    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public JsonResponseEntity<String> remove(Integer memberId) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return response;
    }
}
