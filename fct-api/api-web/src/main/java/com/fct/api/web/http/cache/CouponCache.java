package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.api.web.utils.FctResourceUrl;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.finance.data.entity.PayPlatform;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponCache {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private ProductCache productCache;

    @Autowired
    private FctResourceUrl fctResourceUrl;

    public List<Map<String, Object>> findCacheCanReceive(Integer productId) {
        String key = "api_coupons";
        if (productId > 0)
            key = key + "_" + productId.toString();

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {

                return (List<Map<String, Object>>) SerializationUtils.deserialize(object);
            } else {

                List<Map<String, Object>> lsMap = this.findCanReceive(productId);
                if (lsMap != null && lsMap.size() > 0) {

                    jedis.set(key.getBytes(), SerializationUtils.serialize(lsMap));
                    jedis.expire(key, 1);
                }

                return lsMap;
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }

    public List<Map<String, Object>> findCanReceive(Integer productId) {

        List<CouponPolicy> lsCoupon = promotionService.findCanReceiveCouponPolicy(productId);

        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsCoupon != null) {

            String productIds = "";
            for (CouponPolicy policy: lsCoupon) {

                productIds += (StringUtils.isEmpty(productIds) ? "" : ",") + policy.getProductIds();
            }

            Map<Integer, Object> productMap = this.findProducts(productIds);

            Map<String, Object> map = null;
            List<Map<String, Object>> lsProductMaps = null;
            for (CouponPolicy policy: lsCoupon) {

                lsProductMaps = new ArrayList<>();
                //限定产品使用
                if (policy.getTypeId() > 0 && !StringUtils.isEmpty(policy.getProductIds())) {
                    String[] goodsIds = policy.getProductIds().split(",");
                    if (goodsIds.length > 0) {
                        for (int i = 0; i < goodsIds.length; i++) {
                            Integer goodsId = ConvertUtils.toInteger(goodsIds[i]);
                            if (goodsId > 0 && productMap.get(goodsId) != null) {

                                lsProductMaps.add((Map<String, Object>) productMap.get(goodsId));
                            }
                        }
                    }
                }

                map = new HashMap<>();
                map.put("id", policy.getId());
                map.put("name", policy.getName());
                map.put("startTime", DateUtils.formatDate(policy.getStartTime(),"yyyy.M.d"));
                map.put("endTime", DateUtils.formatDate(policy.getEndTime(),"yyyy.M.d"));
                map.put("amount", policy.getAmount());
                map.put("fullAmount", policy.getFullAmount());
                map.put("status", policy.getAmount());
                map.put("typeId", policy.getTypeId());
                map.put("goods", lsProductMaps);

                lsMaps.add(map);
            }
        }

        return lsMaps;
    }

    public List<Map<String, Object>> findMemberReceive(Integer memberId, Integer status) {

        List<CouponCodeDTO> lsCoupon = promotionService.findMemberCouponCode(0, memberId,
                "", status, false, 1, 20);

        List<Map<String, Object>> lsMaps = new ArrayList<>();
        if (lsCoupon != null) {

            String productIds = "";
            for (CouponCodeDTO policy: lsCoupon) {

                productIds += (StringUtils.isEmpty(productIds) ? "" : ",") + policy.getProductIds();
            }

            Map<Integer, Object> productMap = this.findProducts(productIds);

            Map<String, Object> map = null;
            List<Map<String, Object>> lsProductMaps = null;
            for (CouponCodeDTO policy: lsCoupon) {

                lsProductMaps = new ArrayList<>();
                //限定产品使用
                if (!StringUtils.isEmpty(policy.getProductIds())) {
                    String[] goodsIds = policy.getProductIds().split(",");
                    if (goodsIds.length > 0) {
                        for (int i = 0; i < goodsIds.length; i++) {

                            Integer goodsId = ConvertUtils.toInteger(goodsIds[i]);
                            if (goodsId > 0 && productMap.get(goodsId) != null)
                                lsProductMaps.add((Map<String, Object>) productMap.get(goodsId));
                        }
                    }
                }

                map = new HashMap<>();
                map.put("id", policy.getId());
                map.put("name", policy.getCouponName());
                map.put("startTime", DateUtils.formatDate(policy.getStartTime(),"yyyy.M.d"));
                map.put("endTime", DateUtils.formatDate(policy.getEndTime(),"yyyy.M.d"));
                map.put("amount", policy.getAmount());
                map.put("fullAmount", policy.getFullAmount());
                map.put("status", policy.getAmount());
                map.put("goods", lsProductMaps);

                lsMaps.add(map);
            }
        }

        return lsMaps;
    }

    /**产品列表
     *
     * @param productIds
     * @return
     */
    public Map<Integer, Object> findProducts(String productIds) {

        List<Goods> lsGoods = productCache.findProductByIds(productIds);

        Map<Integer, Object> map = new HashMap<>();
        if (lsGoods != null) {
            Map<String, Object> productMap = null;
            for (Goods goods: lsGoods) {

                productMap = new HashMap<>();
                productMap.put("id", goods.getId());
                productMap.put("name", goods.getName());
                productMap.put("defaultImage", fctResourceUrl.thumbSmall(goods.getDefaultImage()));
                map.put(goods.getId(), productMap);
            }
        }

        return map;
    }
}
