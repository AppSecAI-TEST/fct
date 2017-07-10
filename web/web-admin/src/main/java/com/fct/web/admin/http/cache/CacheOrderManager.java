package com.fct.web.admin.http.cache;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.entity.OrderGoods;
import com.fct.mall.interfaces.MallService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jon on 2017/6/12.
 */
@Service
public class CacheOrderManager {

    @Autowired
    private MallService mallService;

    @Autowired
    private  CacheFinanceManager cacheFinanceManager;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 30;

    public List<OrderGoods> findCacheOrderGoods(String orderId)
    {
        String key = "cache_ordergoods_all_"+orderId;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (List<OrderGoods>) SerializationUtils.deserialize(object);
            }
            else
            {
                List<OrderGoods> ls = findOrderGoods(orderId);
                if (ls != null && ls.size() > 0) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(ls));
                    jedis.expire(key,expireSecond);
                }
                return ls;
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
        return findOrderGoods(orderId);
    }

    public List<OrderGoods> findOrderGoods(String orderId)
    {
        if(StringUtils.isEmpty(orderId))
            return new ArrayList<>();
        try {
            return mallService.findOrderGoods(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList();
    }

    public Map<Integer,String> getStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待付款");
        map.put(1,"付款成功");
        map.put(2,"已发货");
        map.put(3,"交易完成");
        map.put(4,"交易关闭");
        return map;
    }

    public String getStatusName(Integer key)
    {
        return getStatus().get(key);
    }

    public Map<Integer,String> getRefundStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"等待处理");
        map.put(1,"接受申请");
        map.put(2,"用户寄回");
        map.put(3,"同意退款");
        map.put(4,"退款成功");
        map.put(5,"拒绝处理");
        map.put(6,"关闭退换货");
        return map;
    }

    public String getRefundStatusName(Integer key)
    {
        return getRefundStatus().get(key);
    }

    public String getRefundTypeName(Integer type)
    {
        switch (type)
        {
            case 1:
                return "原路返回";
            case 2:
                return "线下退款";
            default:
                return "退回余额";
        }
    }

    public List<PayPlatform> getPayPlatform()
    {
        return cacheFinanceManager.findCachePayPlatform();
    }

    public String getPayPlatformName(String code)
    {
        return cacheFinanceManager.getPayPlatformName(code);
    }
}
