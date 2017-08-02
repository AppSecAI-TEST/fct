package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/mall/materials")
public class ProductMaterialController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductCache productCache;

    /**根据产品里的泥料ids列出所有泥料
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "by-product", method = RequestMethod.GET)
    public ReturnValue<List<Map<String, Object>>> getMaterialsByIds(String ids, Integer product_id) {

        ids = ConvertUtils.toString(ids);
        product_id = ConvertUtils.toInteger(product_id);
        if (StringUtils.isEmpty(ids)) {
            return new ReturnValue<>(404, "泥料不存在");
        }

        if (product_id < 1) {
            return new ReturnValue<>(404, "产品不存在");
        }

        List<GoodsMaterial> lsMaterial = mallService.findMaterialByGoods(ids);

        //重新封装艺人列表
        List<Map<String, Object>> lsMap = new ArrayList<>();
        if (!lsMaterial.isEmpty()) {
            for (GoodsMaterial material : lsMaterial) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", material.getId());
                map.put("name", material.getName());
                map.put("image", fctResourceUrl.thumbSmall(material.getImages()));
                map.put("description", material.getDescription());
                //获取泥料指定个数产品
                map.put("products", productCache.guessProducts(product_id, material.getId(), 0, 3));

                lsMap.add(map);
            }
        }
        ReturnValue<List<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(lsMap);

        return  response;
    }
}
