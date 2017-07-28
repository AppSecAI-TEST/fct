package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ProductCategoryCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-7-3.
 */
@RestController
@RequestMapping(value = "/mall/products/categories")
public class ProductCategoryController extends BaseController {

    @Autowired
    private ProductCategoryCache productCategoryCache;

    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<List<Map<String,String>>> findCategory() {

        List<Map<String,String>> lsMaps = productCategoryCache.findParentCategory();

        ReturnValue<List<Map<String,String>>> response = new ReturnValue<>();
        response.setData(lsMaps);

        return response;
    }
}
