package com.fct.web.admin.utils;

import com.fct.web.admin.session.Session;
import com.fct.core.utils.UUIDUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author ningyang
 */
//@Component("sessionService")
public class SessionUtil {

    private static final String sessionPrefix = "fct:session:";
    private static final String userSessionPrefix = "fct:session:user:";
    private static final int sessionExpireSecond = 60 * 24 * 60 * 60;
    private static final int sessionHistorySize = 2;

    @Autowired
    private JedisPool jedisPool;

    private String generateKeyBy(String accessToken) {
        return sessionPrefix + accessToken;
    }

    public Session get(String accessToken) {
        Map<String, String> objectMap = getSessionFromRedis(generateKeyBy(accessToken));
        if (objectMap != null && objectMap.size() > 0) {
            return new Session(accessToken, objectMap);
        } else {
            return null;
        }
    }

    private Map<String, String> getSessionFromRedis(String token) {
        try(Jedis jedis = jedisPool.getResource()){
            Map<String, String> result = jedis.hgetAll(token);
            if(result!=null)
                jedis.expire(token,sessionExpireSecond);
            return result;
        }
    }

    public Session createUser(String userId) {
        return create(userId);
    }

    private Session create(String id) {
        String accessToken = accessToken();
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        if (id != null) {
            builder.put(Session.Key.id.name(), id);
        }
        builder.put(Session.Key.secret.name(), UUIDUtil.generateUUID());
        builder.put(Session.Key.valid.name(), "1");
        Map<String, String> sessionObjectMap = builder.build();
        createSessionInRedis(generateKeyBy(accessToken), sessionObjectMap);
        return new Session(accessToken, sessionObjectMap);
    }

    public Session createGuest() {
        return createUser(null);
    }

    public void inactiveSession(String token) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.hset(sessionPrefix + token, Session.Key.valid.name(), "0");
        }
    }

    private Map<String, String> createSessionInRedis(String token, Map<String, String> values) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.hmset(token,values);
            jedis.expire(token, sessionExpireSecond);
            return values;
        }
    }

    private void delSessionInRedis(String token) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.del(token);
        }
    }

    private String accessToken() {
        return UUIDUtil.generateUUID();
    }
}
