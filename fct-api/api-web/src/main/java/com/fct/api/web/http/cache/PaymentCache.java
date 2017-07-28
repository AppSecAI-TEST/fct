package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.GoodsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentCache {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FinanceService financeService;

    public String getNameByCode(String code) {

        if (StringUtils.isEmpty(code))
            return "";

        Map<String, String> map = this.findPayment();

        return  map != null ? map.get(code) : "";
    }

    public Map<String, String> findPayment() {

        String key = "pay_platforms";
        List<GoodsCategory> lsCategory = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {

                return (Map<String, String>) SerializationUtils.deserialize(object);
            } else {

                List<PayPlatform> lsPlatform = financeService.findPayPlatform("", "", 1);
                Map<String, String> map = new HashMap<>();
                if (lsPlatform != null) {
                    for (PayPlatform platform: lsPlatform) {

                        map.put(platform.getCode(), platform.getName());
                    }
                }
                if (map != null && map.size() > 0) {

                    jedis.set(key.getBytes(), SerializationUtils.serialize(map));
                    jedis.expire(key, 86400);
                }

                return map;
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }

        return null;
    }
}
