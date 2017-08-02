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

    public WeChatShareResponse jsShare(String url, Boolean debug) {

        //Date date = new Date();
        String noncestr = StringHelper.getRandomString(16);
        Integer timestamp = ConvertUtils.toInteger(System.currentTimeMillis() / 1000);
        debug = ConvertUtils.toBoolean(debug);

        String ticket = getJsTicket();
        if (ticket == null) {
            return null;
        }

        WeChatShareResponse response = new WeChatShareResponse();
        response.setDebug(debug);
        response.setAppId(oAuthCofnig.getAppId());
        response.setTimestamp(timestamp);
        response.setNonceStr(noncestr);
        response.setSignature(signature(ticket, noncestr, timestamp, url));
        Constants.logger.info("response:" + JsonConverter.toJson(response));
        return response;
    }

    private String signature(String ticket, String noncestr, Integer timestamp, String url) {

        String str = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s",
                ticket, noncestr, timestamp, url);

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
                ShareResultResponse response = getRequestData(url);
                if (response != null && !StringUtils.isEmpty(response.getAccess_token())) {
                    jedis.set(key.getBytes(), SerializationUtils.serialize(response.getAccess_token()));
                    jedis.expire(key, 7000); //缓存7200秒，提前一天失效
                    return response.getAccess_token();
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
                Constants.logger.info("redis-data:");
                return (String) SerializationUtils.deserialize(object);
            }
            else
            {
                String accessToken = this.getAccessToken();
                if (StringUtils.isEmpty(accessToken))
                    return  null;

                String url = String.format("%s?type=jsapi&access_token=%s", TICKET_URL, accessToken);
                ShareResultResponse response = getRequestData(url);
                if (response != null && !StringUtils.isEmpty(response.getTicket())) {
                    jedis.set(key.getBytes(), SerializationUtils.serialize(response.getTicket()));
                    jedis.expire(key, 7000); //缓存7200秒，提前一天失效
                    return response.getTicket();
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

    private ShareResultResponse getRequestData(String url) throws Exception {

        if (StringUtils.isEmpty(url)) return null;

        HttpClient hc = new HttpClient(url,10000,30000);

        int status = hc.sendGet("UTF-8");

        if (200 == status) {
            String resultString = hc.getResult();
            Constants.logger.info("wecharShare--request-result:"+resultString);
            if (!StringUtils.isEmpty(resultString)) {

                ShareResultResponse response = JsonConverter.toObject(resultString, ShareResultResponse.class);

                if (response.getErrcode() == 0) {

                    return response;
                }

            }
        }else{

            Constants.logger.error("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
        }
        return null;
    }

}
