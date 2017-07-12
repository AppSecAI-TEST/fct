package com.fct.api.web.http.cache;

import com.fct.api.web.utils.Constants;
import com.fct.member.interfaces.MemberDTO;
import com.fct.member.interfaces.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by z on 17-7-6.
 */

@Service
public class MemberCache  {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    private MemberService memberService;

    public MemberDTO getMember(Integer memberId)
    {
        MemberDTO member = null;
        String key = "member_" + memberId;
        Jedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if(object != null)
            {
                return (MemberDTO) SerializationUtils.deserialize(object);
            }
            else
            {
                member = memberService.getMemberDTO(memberId);
                if (member != null) {
                    jedis.set(key.getBytes(), SerializationUtils.serialize(member));
                    //jedis.expire(key,expireSecond);
                }
                return member;
            }
        }
        catch (Exception e)
        {
            Constants.logger.error(e.toString());
        }
        finally {

            if (jedis != null) jedis.close();
        }

        return  memberService.getMemberDTO(memberId);

    }
}
