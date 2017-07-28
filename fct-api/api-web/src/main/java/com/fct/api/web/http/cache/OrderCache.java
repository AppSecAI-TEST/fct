package com.fct.api.web.http.cache;

import com.fct.api.web.config.FctConfig;
import com.fct.api.web.utils.Constants;
import com.fct.api.web.utils.FctResourceUrl;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.member.interfaces.MemberDTO;
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
public class OrderCache {

    @Autowired
    private FctResourceUrl fctResourceUrl;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private MallService mallService;

    public PageResponse<Map<String, Object>> findPageOrder(Integer memberId, String orderId,
                                                           String goodsName, Integer status,
                                                           Integer commentStatus, Integer pageIndex, Integer pageSize) {

        PageResponse<Orders> pageResponse = mallService.findOrdersByAPI(memberId, orderId,
                0, goodsName, status, commentStatus, pageIndex, pageSize);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (pageResponse != null) {
            if (pageResponse.getTotalCount() > 0) {

                List<Map<String, Object>> lsMaps = new ArrayList<>();
                Map<String, Object> map = null;

                List<Map<String, Object>> lsProductMaps = null;
                Map<String, Object> productMap = null;

                for (Orders order: pageResponse.getElements()) {

                    map = new HashMap<>();
                    map.put("orderId", order.getOrderId());
                    map.put("payAmount", order.getPayAmount());
                    map.put("status", order.getStatus());
                    map.put("statusName", this.getStatusName(order.getStatus()));
                    map.put("buyTotalCount", order.getOrderGoods().size());
                    lsProductMaps = new ArrayList<>();
                    for (OrderGoods product: order.getOrderGoods()) {

                        productMap = new HashMap<>();
                        productMap.put("name", product.getName());
                        productMap.put("specName", product.getSpecName());
                        productMap.put("img", fctResourceUrl.getImageUrl(product.getImg()));
                        productMap.put("buyCount", product.getBuyCount());
                        productMap.put("price", product.getPrice());
                        lsProductMaps.add(productMap);
                    }
                    map.put("orderGoods", lsProductMaps);
                    lsMaps.add(map);
                }

                pageMaps.setElements(lsMaps);
                pageMaps.setCurrent(pageResponse.getCurrent());
                pageMaps.setTotalCount(pageResponse.getTotalCount());
                pageMaps.setHasMore(pageResponse.isHasMore());
            }
        }

        return pageMaps;
    }

    public Orders getOrder(String orderId) {

        String key = "api_order_" + orderId;
        Jedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (Orders) SerializationUtils.deserialize(object);
            }
            else
            {
                Orders order = mallService.getOrders(orderId);
                if (order != null && order.getStatus() == 3) {
                    jedis.set(key.getBytes(), SerializationUtils.serialize(order));
                    jedis.expire(key, 259200);
                }
                return order;
            }
        }
        catch (Exception e)
        {
            Constants.logger.error(e.toString());
        }
        finally {

            if (jedis != null) jedis.close();
        }

        return  null;
    }

    public String getStatusName(Integer status) {

        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待发货";
            case 2:
                return "待收货";
            case 3:
                return "交易完成";
            case 4:
                return "关闭";
        }

        return "";
    }
}
