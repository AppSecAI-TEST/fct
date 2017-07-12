package com.fct.common.service.oauth;

import com.fct.common.interfaces.WeChatResponse;
import com.fct.common.interfaces.WeChatShareResponse;
import com.fct.common.interfaces.WeChatUserResponse;
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

/**
 * Created by z on 17-7-12.
 */

@Service
public class WeChat {

    @Autowired
    private OAuthCofnig oAuthCofnig;

    @Autowired
    private JedisPool jedisPool;


    private final String CONNECT_URL = "https://open.weixin.qq.com/connect/oauth2";

    private final String SNS_URL = "https://api.weixin.qq.com/sns";

    private final String JS_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";



    public String oAuthURL(String redirectURI, String scope) {

        return String.format(
                "%s/authorize?appid=%s&response_type=code&scope=%s&state=fangcun&redirect_uri=%s#wechat_redirect",
                CONNECT_URL, oAuthCofnig.getAppId(), scope, redirectURI);
    }

    public WeChatResponse callback(String code) {

        String url = String.format(
                "%s/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                SNS_URL, oAuthCofnig.getAppId(), oAuthCofnig.getAppSecret(), code);

        String resultString = this.get(url);

        if (StringUtils.isEmpty(resultString)) return null;

        Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
        //请求出错，记录错误信息
        if (result.containsKey("errcode"))
            Constants.logger.error("code:" + result.get("errcode") + ", message:" + result.get("errmsg"));

        String openid = ConvertUtils.toString(result.get("openid"));
        String accessToken = ConvertUtils.toString(result.get("access_token"));
        String refreshToken = ConvertUtils.toString(result.get("refresh_token"));
        Integer expireIn = ConvertUtils.toInteger(result.get("expires_in"));

        WeChatResponse response = new WeChatResponse();
        response.setOpenid(openid);
        response.setAccessToken(accessToken);

        this.setAccessToken(accessToken, refreshToken, expireIn);

        return response;
    }

    /**获取用户信息
     *
     * @param openId
     * @return
     */
    public WeChatUserResponse getUserInfo(String openId) {

        String url = String.format(
                "%s/userinfo?access_token=%s&openid=%s&lang=zh_CN",
                SNS_URL, this.getAccessToken(), openId);

        String resultString = this.get(url);
        if (resultString != null) {
            Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
            if (result == null) {
                return  null;
            }

            WeChatUserResponse weChatResponse = new WeChatUserResponse();
            weChatResponse.setOpenid(openId);
            weChatResponse.setNickname(ConvertUtils.toString(result.get("nickname")));
            weChatResponse.setSex(ConvertUtils.toInteger(result.get("sex")) == 2 ? 0 : 1);
            weChatResponse.setHeadimgurl(ConvertUtils.toString(result.get("headimgurl")));
            weChatResponse.setUnionid(ConvertUtils.toString(result.get("unionid")));
            weChatResponse.setContry(ConvertUtils.toString(result.get("contry")));
            weChatResponse.setProvince(ConvertUtils.toString(result.get("province")));
            weChatResponse.setCity(ConvertUtils.toString(result.get("city")));

            return weChatResponse;
        }

        return null;
    }

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

                if (StringUtils.isEmpty(accessToken)) return  null;

                String url = String.format("%s?access_token=%s&type=jsapi", JS_TICKET, accessToken);

                String resultString = this.get(url);
                if (StringUtils.isEmpty(resultString))  return  null;

                Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
                if (result.containsKey("errcode")) {

                    Constants.logger.error("code:" + result.get("errcode") + ", message:" + result.get("errmsg"));
                    return null;
                }

                if (result != null) {

                    String ticket = ConvertUtils.toString(result.get("ticket"));

                    jedis.set(key.getBytes(), SerializationUtils.serialize(ticket));
                    jedis.expire(key, ConvertUtils.toInteger(result.get("expires_in")) - 5); //缓存7200秒，提前一天失效
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


    /**获取accessToken
     *
     * @return
     */
    private String getAccessToken() {

        String key = "wechat_access_token";
        Jedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            byte[] object = jedis.get((key).getBytes());
            WeChatSource weChatSource = null;
            //对象不存在或不是刷新请求
            if(object != null)
            {
                weChatSource = (WeChatSource) SerializationUtils.deserialize(object);
                Date date = new Date();
                Integer cacheTime = ConvertUtils.toInteger(date.getTime() - weChatSource.getRefreshTime() / 1000);
                if (cacheTime >= weChatSource.getExpireIn()) {

                    String url = String.format(
                            "%s/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s",
                            SNS_URL, oAuthCofnig.getAppId(), weChatSource.getRefreshToken());

                    String resultString = this.get(url);

                    if (StringUtils.isEmpty(resultString)) return null;

                    Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
                    //请求出错，记录错误信息
                    if (result.containsKey("errcode"))  Constants.logger.error(
                            "code:" + result.get("errcode") + ", message:" + result.get("errmsg"));

                    weChatSource.setAccessToken(ConvertUtils.toString(result.get("access_token")));
                    //过期时间，通常为7200
                    weChatSource.setExpireIn(ConvertUtils.toInteger(result.get("expire_in")));
                    //刷新的token，有效期为30天
                    weChatSource.setRefreshToken(ConvertUtils.toString(result.get("refresh_token")));
                    //刷新时间
                    weChatSource.setRefreshTime(date.getTime());

                    jedis.set(key.getBytes(), SerializationUtils.serialize(weChatSource));
                }

                return weChatSource.getAccessToken();
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

    /**缓存accesstoken和刷新key
     *
     * @param accessToken
     * @param refreshToken
     * @param expireIn
     */
    private void setAccessToken(String accessToken, String refreshToken, Integer expireIn) {

        String key = "wechat_access_token";
        Jedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();

            Date date = new Date();
            WeChatSource weChatSource = new WeChatSource();
            weChatSource.setAccessToken(accessToken);
            //过期时间，通常为7200
            weChatSource.setExpireIn(expireIn);
            //刷新的token，有效期为30天
            weChatSource.setRefreshToken(refreshToken);
            //刷新时间
            weChatSource.setRefreshTime(date.getTime());

            jedis.set(key.getBytes(), SerializationUtils.serialize(weChatSource));
            jedis.expire(key, 2505600); //缓存29天，提前一天失效
        }
        catch (Exception e)
        {
            Constants.logger.error(e.toString());
        }
        finally {

            if (jedis != null) jedis.close();
        }
    }

    private String get(String url) {

        if (StringUtils.isEmpty(url)) return null;

        HttpClient hc = new HttpClient(url,10000,30000);
        try {
            int status = hc.sendGet("UTF-8");
            if (200 == status) {
                String resultString = hc.getResult();
                if (!StringUtils.isEmpty(resultString)) {

                    return resultString;
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
