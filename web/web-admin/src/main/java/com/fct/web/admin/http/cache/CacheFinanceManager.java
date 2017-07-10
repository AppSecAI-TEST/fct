package com.fct.web.admin.http.cache;

import com.fct.artist.data.entity.Artist;
import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.interfaces.FinanceService;
import com.fct.web.admin.utils.Constants;
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
public class CacheFinanceManager {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheOrderManager cacheOrderManager;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 35; //35分钟

    public List<PayPlatform> findCachePayPlatform()
    {
        String key = "cache_payplatform";
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<PayPlatform>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<PayPlatform> artist = findPayPlatform();
                if (artist != null) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(artist));
                    jedis.expire(key,expireSecond);
                }
                return artist;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        finally {
            if(jedis != null)
            {
                jedis.close();
            }
        }
        return findPayPlatform();
    }


    public List<PayPlatform> findPayPlatform()
    {
        try {
            return financeService.findPayPlatform("");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getPayPlatformName(String code)
    {
        List<PayPlatform> list = findCachePayPlatform();
        for (PayPlatform pay: list
                ) {
            if(pay.getCode().equals(code))
                return pay.getName();
        }
        return "";
    }

    public Map<String,String> getTradeType()
    {
        Map<String,String> map = new HashMap<>();
        map.put("recharge","充值");
        map.put("buy","购买宝贝");
        map.put("withdraw","提现");
        map.put("settle","销售结算");
        map.put("refund","退款");
        map.put("withdraw_refund","提现退款");
        return map;
    }

    public String getTradeTypeName(String key)
    {
        return getTradeType().get(key);
    }

    public Map<Integer,String> getPayStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待付款");
        map.put(1,"支付成功");
        map.put(2,"超时关闭");
        map.put(3,"支付异常");
        map.put(4,"全部退款");
        return map;
    }

    public String getPayStatusName(Integer key)
    {
        return getPayStatus().get(key);
    }

    public Map<Integer,String> getSettleStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待确认");
        map.put(1,"财务已确认");
        map.put(2,"结算成功");
        map.put(3,"拒绝");
        return map;
    }

    public String getSettleStatusName(Integer key)
    {
        return getSettleStatus().get(key);
    }

    public String getRefundTypeName(Integer type)
    {
        return cacheOrderManager.getRefundTypeName(type);
    }

    public Map<Integer,String> getRefundStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待处理");
        map.put(1,"财务已确认");
        map.put(2,"退款成功");
        map.put(3,"关闭退款");
        return map;
    }

    public String getRefundStatusName(Integer key)
    {
        return getRefundStatus().get(key);
    }
}
