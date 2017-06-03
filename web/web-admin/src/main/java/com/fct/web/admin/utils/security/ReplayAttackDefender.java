package com.fct.web.admin.utils.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * @author ningyang
 */
//@Component
public class ReplayAttackDefender {

    private static final String prefix = "req:";
    @Autowired
    private JedisPool jedisPool;

    public Boolean check(String requestId) {
        String key = prefix + requestId;
        try(Jedis jedis = jedisPool.getResource()){
            if(jedis.exists(key)){
                return false;
            }else{
                jedis.setex(key, 600, "");
                return true;
            }
        }
    }
}
