package com.fct.common.service.oauth;

import com.fct.common.interfaces.WeChatShareResponse;
import com.fct.common.service.Constants;
import com.fct.common.service.OAuthCofnig;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.StringHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

@Service
public class WeChatShare {

    private final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    private final String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    @Autowired
    private OAuthCofnig oAuthCofnig;

    @Autowired
    private JedisPool jedisPool;


    public WeChatShareResponse jsShare(String url) {

        Date date = new Date();
        String noncestr = StringHelper.getRandomString(16);
        Integer timestamp = ConvertUtils.toInteger(date.getTime() / 1000);
        String ticket = this.getJsTicket();
        if (ticket == null) {

            return null;
        }

        WeChatShareResponse response = new WeChatShareResponse();
        response.setDebug(false);
        response.setAppId(oAuthCofnig.getAppId());
        response.setTimestamp(timestamp);
        response.setNonceStr(noncestr);
        response.setSignature(this.signature(ticket, noncestr, timestamp, url));

        return response;
    }

    private String signature(String ticket, String noncestr, Integer timestamp, String url) {

        String str = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s?params=value",
                ticket, noncestr, timestamp, url).toLowerCase();

        return DigestUtils.sha1Hex(str);
    }

    public String getAccessToken() {

        String key = "wechat_js_access_token";
        Jedis jedis = null;
        try {

            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            if (object != null) {
                return (String) SerializationUtils.deserialize(object);
            } else {

                String url = String.format("%s?grant_type=client_credential&appid=%s&secret=%s",
                        this.ACCESS_TOKEN_URL, oAuthCofnig.getAppId(), oAuthCofnig.getAppSecret());
                Map<String, Object> result = this.get(url);
                if (result != null) {

                    String accessToken = ConvertUtils.toString(result.get("access_token"));
                    if (StringUtils.isEmpty(accessToken)) {
                        return null;
                    }
                    jedis.set(key.getBytes(), SerializationUtils.serialize(accessToken));
                    jedis.expire(key, 7000); //缓存7200秒，提前一天失效
                    return accessToken;
                }
            }
        } catch (Exception e) {
            Constants.logger.error(e.toString());
        } finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }


    private String getJsTicket() {


        String key = "wechat_js_ticket";
        Jedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            //对象不存在或不是刷新请求
            if(object != null)
            {
                return (String) SerializationUtils.deserialize(object);
            }
            else
            {
                String accessToken = this.getAccessToken();
                if (StringUtils.isEmpty(accessToken))
                    return  null;

                String url = String.format("%s?type=jsapi&access_token=%s", TICKET_URL, accessToken);
                Map<String, Object> result = this.get(url);
                if (result != null) {

                    String ticket = ConvertUtils.toString(result.get("ticket"));
                    if (StringUtils.isEmpty(ticket))
                        return null;

                    jedis.set(key.getBytes(), SerializationUtils.serialize(ticket));
                    jedis.expire(key, 7000); //缓存7200秒，提前一天失效
                    return ticket;
                }
            }
        }
        catch (Exception e)
        {
            Constants.logger.error(e.toString());
        }
        finally {

            if (jedis != null) jedis.close();
        }

        return null;
    }

    private Map<String, Object> get(String url) {

        if (StringUtils.isEmpty(url)) return null;

        HttpClient hc = new HttpClient(url,10000,30000);
        try {
            int status = hc.sendGet("UTF-8");
            if (200 == status) {
                String resultString = hc.getResult();
                if (!StringUtils.isEmpty(resultString)) {

                    Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
                    if (result.containsKey("errcode")) {

                        Constants.logger.error("code:" + result.get("errcode") + ", message:" + result.get("errmsg"));
                        return null;
                    }

                    return result;
                }
            }else{

                Constants.logger.error("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
            }
        } catch (Exception e) {

            Constants.logger.error(e.getMessage(), e);
        }

        return null;
    }

}
