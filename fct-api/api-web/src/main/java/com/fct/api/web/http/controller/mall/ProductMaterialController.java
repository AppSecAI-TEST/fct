package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/materials")
public class ProductMaterialController extends BaseController {

    @Autowired
    private MallService mallService;

    /**根据产品里的泥料ids列出所有泥料
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "by-product", method = RequestMethod.GET)
    public ReturnValue<List<GoodsMaterial>> getMaterialsByIds(String ids) {

        ids = ConvertUtils.toString(ids);

        List<GoodsMaterial> lsMaterial = mallService.findMaterialByGoods(ids);

        ReturnValue<List<GoodsMaterial>> response = new ReturnValue<>();
        response.setData(lsMaterial);

        return  response;
    }
}
