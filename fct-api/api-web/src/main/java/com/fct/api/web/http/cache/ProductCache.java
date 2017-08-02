package com.fct.api.web.http.cache;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.core.utils.ConvertUtils;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-7-7.
 */

@Service
public class ProductCache {

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private MallService mallService;

    public PageResponse<Map<String, Object>> findShareGoods(String name, String code, Integer sortIndex, Integer pageIndex, Integer pageSize) {

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();

        PageResponse<Goods> pageResponse = mallService.shareGoods(name, code, sortIndex, pageIndex, pageSize);

        if (pageResponse != null && pageResponse.getTotalCount() > 0) {
            List<Map<String, Object>> lsMap = new ArrayList<>();
            for (Goods goods:pageResponse.getElements()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goods.getId());
                map.put("name", goods.getName());
                map.put("defaultImage", fctResourceUrl.thumbSmall(goods.getDefaultImage()));
                if (goods.getSpecification() == null || goods.getSpecification().size() < 1) {
                    map.put("price", goods.getSalePrice());
                    map.put("commission", goods.getCommission());
                } else {
                    BigDecimal minPrice = new BigDecimal(0);
                    BigDecimal maxPrice = new BigDecimal(0);
                    BigDecimal minCommission = new BigDecimal(0);
                    BigDecimal maxCommission = new BigDecimal(0);

                    for (GoodsSpecification specification: goods.getSpecification()) {

                        if (minPrice.doubleValue() <= 0) {

                            minPrice = specification.getSalePrice();
                            minCommission = specification.getCommission();
                        }else if (minPrice.doubleValue() > specification.getSalePrice().doubleValue()) {

                            minPrice = specification.getSalePrice();
                            minCommission = specification.getCommission();
                        }

                        if (maxPrice.doubleValue() < specification.getSalePrice().doubleValue()) {

                            maxPrice = specification.getSalePrice();
                            maxCommission = specification.getCommission();
                        }
                    }

                    List lsPrice = new ArrayList();
                    lsPrice.add(minPrice);
                    lsPrice.add(maxPrice);
                    map.put("price", lsPrice);

                    lsPrice = new ArrayList();
                    lsPrice.add(minCommission);
                    lsPrice.add(maxCommission);
                    map.put("commission", lsPrice);
                }

                lsMap.add(map);
            }

            pageMaps.setElements(lsMap);
            pageMaps.setCurrent(pageResponse.getCurrent());
            pageMaps.setTotalCount(pageResponse.getTotalCount());
            pageMaps.setHasMore(pageResponse.isHasMore());
        }

        return pageMaps;
    }

    /**通过分类code获取产品列表
     *
     * @param code
     * @param skip
     * @return
     */
    public List<Map<String, Object>> guessProductsByCategoryCode(String code, int skip) {
        PageResponse<Goods> pageResponse = mallService.findGoods("", code, 0,
                0, 0, 0, 0, 1, 1, skip);

        if (pageResponse == null) return  null;
        if (pageResponse.getTotalCount() < 1)  return  null;

        return this.listGoodsMap(pageResponse.getElements(), false, false);
    }

    /**PC首页产品
     *
     * @param skip
     * @return
     */
    public List<Map<String, Object>> pcHomeProducts(int skip) {

        return this.findGuessProducts("", 0, 0, skip, false, true);
    }


    /**艺术家作品列表
     *
     * @param artistId
     * @param skip
     * @return
     */
    public List<Map<String, Object>> artistProducts(Integer artistId, int skip) {

        return this.findGuessProducts("", 0, artistId, skip, true, true);
    }

    /**泥料或艺术家获取产品列表
     *
     * @param productId
     * @param materialId
     * @param artistId
     * @param skip
     * @return
     */
    public List<Map<String, Object>> guessProducts(Integer productId, Integer materialId,
                                                   Integer artistId, int skip) {

        String productIds = productId > 0 ? ConvertUtils.toString(productId) : "";
        return this.findGuessProducts(productIds, materialId, artistId, skip, false, false);
    }


    /**过滤指定产品ID获取新的列表
     *
     * @param productIds
     * @param skip
     * @return
     */
    public List<Map<String, Object>> guessProducts(String productIds, int skip) {

        return this.findGuessProducts(productIds, 0, 0, skip, true, false);
    }

    /**猜你喜欢产品列表
     *
     * @param productIds
     * @param materialId
     * @param artistId
     * @param skip
     * @param hasPrice
     * @param hasIntro
     * @return
     */
    private List<Map<String, Object>> findGuessProducts(String productIds, Integer materialId,
                                                       Integer artistId, int skip, Boolean hasPrice,
                                                        Boolean hasIntro) {
        List<Goods> lsGoods = mallService.findGoodsByGuess(productIds, "",
                0, materialId, artistId, 3);

        return this.listGoodsMap(lsGoods, hasPrice, hasIntro);
    }

    /**把产品转换成指定列表
     *
     * @param lsGoods
     * @param hasPrice
     * @param hasIntro
     * @return
     */
    private List<Map<String, Object>> listGoodsMap(List<Goods> lsGoods, Boolean hasPrice, Boolean hasIntro) {
        //获取艺术指定个数产品
        List<Map<String, Object>> lsMap = new ArrayList<>();
        if (!lsGoods.isEmpty()) {
            for (Goods goods:lsGoods) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goods.getId());
                map.put("name", goods.getName());
                map.put("defaultImage", fctResourceUrl.thumbMedium(goods.getDefaultImage()));

                if (hasPrice)
                    map.put("price", goods.getSalePrice());

                if (hasIntro)
                    map.put("intro", goods.getIntro());


                lsMap.add(map);
            }
        }

        return lsMap;
    }

    public List<Goods> findProductByIds(String productIds) {

        return mallService.findGoodsByIds(productIds);
    }
}
