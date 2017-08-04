package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.cache.ProductCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.DiscountProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/*
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*/

/**
 * Created by z on 17-6-23.
 */

@RestController
@RequestMapping(value = "/mall/products")
public class ProductController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private ProductCache productCache;

    /**获取产品详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getProduct(@PathVariable("id") Integer id) {

        id= ConvertUtils.toInteger(id);
        if (id < 1) {
            return new ReturnValue<>(404, "产品不存在");
        }

        Integer favoriteState = 0;
        Integer cartProductCount = 0;
        Integer hasCoupon = 0;
        Map<String, Object> product = new HashMap<>();
        Goods goods = null;
        //是否开始
        Boolean hasBegin = false;
        //是否能买
        Boolean notCanBuy = false;
        DiscountProductDTO discountProductDTO =null;

        MemberLogin member = this.memberAuth(false);

        hasCoupon = promotionService.getReceiveCountByProduct(id);
        if (member != null) {
            favoriteState = memberService.getFavouriteCount(member.getMemberId(), 0, id);
            cartProductCount = mallService.getShoopingCartCount(member.getMemberId());
        }
        goods = mallService.getGoods(id);
        if(goods == null)
        {
            return new ReturnValue<Map<String, Object>>(404, "产品不存在");
        }

        mallService.addGoodsViewCount(goods.getId(), 1);

        discountProductDTO = promotionService.getDiscountByProduct(id);
        Map<String, Object> discount = new HashMap<>();
        if (discountProductDTO != null) {
            Long endTime = DateUtils.compareDate(discountProductDTO.getDiscount().getStartTime(), new Date()) / 1000;
            //活动开始了
            if (endTime <= 0) {

                endTime = DateUtils.compareDate(discountProductDTO.getDiscount().getEndTime(), new Date()) / 1000;
                hasBegin = true;

            }
            //活动未开始是否限制购买
            else if (discountProductDTO.getDiscount().getNotStartCanNotBuy() == 1) {

                notCanBuy = true;
            }

            discount.put("id", discountProductDTO.getDiscount().getId());
            discount.put("name", discountProductDTO.getDiscount().getName());
            discount.put("discountRate", discountProductDTO.getDiscountProduct().getDiscountRate());
            discount.put("discountTime", endTime);
            discount.put("hasBegin", hasBegin);
            discount.put("canBuy", !notCanBuy);
        }

        //活动开始或限制购买，价格变成折扣后价格
        BigDecimal discountRate = ConvertUtils.toBigDeciaml(
                (hasBegin || notCanBuy)
                        ? discountProductDTO.getDiscountProduct().getDiscountRate()
                        : 1);

        product.put("id", goods.getId());
        product.put("name", goods.getName());
        product.put("subTitle", goods.getSubTitle());
        product.put("status", goods.getStatus());
        //如果是限制购买，在活动未开始之前禁止购买，库存为0
        product.put("stockCount", notCanBuy ? 0 : goods.getStockCount());
        product.put("salePrice", goods.getSalePrice());
        product.put("promotionPrice", goods.getSalePrice().multiply(discountRate));
        product.put("favoriteState", favoriteState);
        product.put("cartProductCount", cartProductCount);
        product.put("hasCoupon", hasCoupon);
        product.put("hasDiscount", discountProductDTO != null);
        product.put("discount", discount);
        product.put("materialId", goods.getMaterialIds());
        product.put("content", goods.getContent());

        Map<String, Object> video = new HashMap<>();
        video.put("url", goods.getVideoUrl());
        video.put("poster", fctResourceUrl.thumbLarge(goods.getVideoImg()));
        product.put("video", video);

        List<Map<String, Object>> lsSpec = new ArrayList<>();
        for (GoodsSpecification spec:goods.getSpecification()) {
            Map<String, Object> specification = new HashMap<>();
            specification.put("id", spec.getId());
            specification.put("name", spec.getName());
            //如果是限制购买，在活动未开始之前禁止购买，库存为0
            specification.put("stockCount", notCanBuy ? 0 : spec.getStockCount());
            specification.put("salePrice", spec.getSalePrice());
            specification.put("promotionPrice", spec.getSalePrice().multiply(discountRate));
            lsSpec.add(specification);
        }

        product.put("specification", lsSpec);

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(product);

        return  response;
    }

    /**通过艺术家ID获取作品列表
     *
     * @param artist_id
     * @return
     */
    @RequestMapping(value = "by-artist", method = RequestMethod.GET)
    public ReturnValue<List<Map<String, Object>>> getProductsByArtistId(Integer artist_id) {

        artist_id = ConvertUtils.toInteger(artist_id);
        if (artist_id < 1) {
            return new ReturnValue<>(404, "艺术家不存在");
        }

        ReturnValue<List<Map<String, Object>>> response = new ReturnValue<>();
        List<Map<String, Object>> lsMaps = productCache.artistProducts(artist_id, 100);

        response.setData(lsMaps);

        return response;
    }

    @RequestMapping(value = "share", method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findShareProduct(String name, String code, Integer sort_index,
                                                              Integer page_index, Integer page_size) {
        name = ConvertUtils.toString(name);
        code = ConvertUtils.toString(code);
        sort_index = ConvertUtils.toInteger(sort_index, 0);
        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        MemberLogin member = this.memberAuth();
        if (member.getShopId() < 1) {

            return new ReturnValue<>(404, "没有权限使用此功能");
        }

        PageResponse<Map<String, Object>> pageMaps = productCache.findShareGoods(name, code,
                sort_index, page_index, page_size);

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return  response;
    }
}
