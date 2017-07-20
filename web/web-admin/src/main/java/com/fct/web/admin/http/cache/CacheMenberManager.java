package com.fct.web.admin.http.cache;

import com.fct.member.interfaces.MemberDTO;
import com.fct.member.interfaces.MemberService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by jon on 2017/7/20.
 */
@Service
public class CacheMenberManager {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    private int expireSecond = 60 * 120; //120分钟

    public MemberDTO getCacheMember(Integer id)
    {
        String key = "cache_member_"+id;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (MemberDTO) SerializationUtils.deserialize(object);
            }
            else
            {
                MemberDTO member = getMember(id);
                if (member != null) {
                    jedis.set(key.getBytes(),SerializationUtils.serialize(member));
                    jedis.expire(key,expireSecond);
                }
                return member;
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
        return getMember(id);
    }

    private MemberDTO getMember(Integer id)
    {
        try {
            if (id > 0) {
                return memberService.getMemberDTO(id);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new MemberDTO();
    }
}
