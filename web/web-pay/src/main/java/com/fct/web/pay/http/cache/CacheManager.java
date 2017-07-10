package com.fct.web.pay.http.cache;

import com.fct.core.utils.CookieUtil;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.web.pay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;

@Service
public class CacheManager {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MallService mallService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 24 * 60; //1天

    public MemberLogin getCacheMemberLogin(String token)
    {
        String key = "cache_memberlogin_"+token;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (MemberLogin) SerializationUtils.deserialize(object);
            }
            else
            {
                MemberLogin login = getMemberLogin(token);
                if (login != null ) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(login));
                    jedis.expire(key,expireSecond);
                }
                return login;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return getMemberLogin(token);
    }

    public void removeCacheMemberLogin(HttpServletRequest request)
    {
        String token = CookieUtil.getCookieByName(request,"fct_auth");
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.del(token.getBytes());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

    }

    private MemberLogin getMemberLogin(String token) {
        try
        {
            return memberService.getMemberLogin(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Orders getCacheOrders(String orderId)
    {
        String key = "cache_orders_"+orderId;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (Orders) SerializationUtils.deserialize(object);
            }
            else
            {
                Orders orders = getOrders(orderId);
                if (orders != null ) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(orders));
                    jedis.expire(key,60*5);
                }
                return orders;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return getOrders(orderId);
    }

    private Orders getOrders(String orderId) {
        try
        {
            return mallService.getOrders(orderId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public RechargeRecord getCacheRechargeRecord(Integer id)
    {
        String key = "cache_rechargerecord_"+id;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (RechargeRecord) SerializationUtils.deserialize(object);
            }
            else
            {
                RechargeRecord rechargeRecord = getRechargeRecord(id);
                if (rechargeRecord != null ) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(rechargeRecord));
                    jedis.expire(key,60*5);
                }
                return rechargeRecord;
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return getRechargeRecord(id);
    }

    private RechargeRecord getRechargeRecord(Integer id) {
        try
        {
            return financeService.getRechargeRecord(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}
