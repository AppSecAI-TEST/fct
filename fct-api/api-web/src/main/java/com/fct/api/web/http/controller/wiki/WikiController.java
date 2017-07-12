package com.fct.api.web.http.controller.wiki;

import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.cache.ProductCategoryCache;
import com.fct.api.web.http.cache.ProductMaterialCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-7-11.
 */
@RestController
@RequestMapping(value = "wiki")
public class WikiController extends BaseController {

    @Autowired
    private ProductCategoryCache productCategoryCache;

    @Autowired
    private ProductMaterialCache productMaterialCache;

    @Autowired
    private ProductCache productCache;


    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> wikiHome()
    {

        List<Map<String, Object>> lsCategory = productCategoryCache.findWikiCategory();
        List<Map<String, Object>> lsMaterial = productMaterialCache.findWikiMaterial();

        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", lsCategory);
        map.put("materialList", lsMaterial);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }

    @RequestMapping(value = "item", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> wikiDetail(Integer type_id, String type) {

        type_id = ConvertUtils.toInteger(type_id);
        type = ConvertUtils.toString(type);

        Map<String, Object> map = null;

        if (type.equals("category")) {

            map = productCategoryCache.getWiki(type_id);
            if (map != null) {

                map.remove(map.get("code"));
                map.put("productList", productCache.guessProductsByCategoryCode(ConvertUtils.toString(map.get("code")), 3));
            }
        } else if (type.equals("material")) {

            map = productCategoryCache.getWiki(type_id);
            if (map != null)

                map.put("productList", productCache.guessProducts(0, type_id, 0, 3));
        }

        if (map == null) return new ReturnValue<>(404, "此类型不存在");

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }
}
