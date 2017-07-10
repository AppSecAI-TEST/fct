package com.fct.web.admin.http.cache;

import com.fct.mall.data.entity.OrderGoods;
import com.fct.member.data.entity.SysUserLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class CacheSysUserManager {
    @Autowired
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 60;

    public SysUserLogin getCacheSysUserLogin(String token)
    {
        String key = "cache_sysuserlogin_"+token;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (SysUserLogin) SerializationUtils.deserialize(object);
            }
            else
            {
                SysUserLogin login = getSysUserLogin(token);
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
        return getSysUserLogin(token);
    }

    public SysUserLogin getSysUserLogin(String token) {
        try
        {
            return memberService.getSysUserLogin(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}
