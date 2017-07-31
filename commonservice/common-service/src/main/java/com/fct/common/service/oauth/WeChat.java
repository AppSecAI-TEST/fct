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


    private final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    private final String USERINFO_URL = "https://api.weixin.qq.com/sns/oauth2/userinfo";

    private final String SNS_URL = "https://api.weixin.qq.com/sns";

    public String oAuthURL(String redirectURI, String scope) {

        return String.format(
                "%s?appid=%s&redirect_uri=%sresponse_type=code&scope=%s&state=fangcun&#wechat_redirect",
                AUTHORIZE_URL, oAuthCofnig.getAppId(), redirectURI, scope);
    }

    public WeChatResponse callback(String code) {

        String url = String.format(
                "%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                ACCESS_TOKEN_URL, oAuthCofnig.getAppId(), oAuthCofnig.getAppSecret(), code);
        Map<String, Object> result = this.get(url);

        WeChatResponse response = new WeChatResponse();
        if (result != null) {
            String openid = ConvertUtils.toString(result.get("openid"));
            String accessToken = ConvertUtils.toString(result.get("access_token"));
            String refreshToken = ConvertUtils.toString(result.get("refresh_token"));
            Integer expireIn = ConvertUtils.toInteger(result.get("expires_in"));

            response.setOpenid(openid);
            response.setAccessToken(accessToken);
            this.setAccessToken(accessToken, refreshToken, expireIn);
        }
        return response;
    }

    /**获取用户信息
     *
     * @param openId
     * @return
     */
    public WeChatUserResponse getUserInfo(String openId) {

        if (this.getAccessToken() == null)
            return  null;

        String url = String.format(
                "%s?access_token=%s&openid=%s&lang=zh_CN",
                USERINFO_URL, this.getAccessToken(), openId);
        Map<String, Object> result = this.get(url);

        WeChatUserResponse weChatResponse = new WeChatUserResponse();
        if (result != null) {

            weChatResponse.setOpenid(openId);
            weChatResponse.setNickname(ConvertUtils.toString(result.get("nickname")));
            weChatResponse.setSex(ConvertUtils.toInteger(result.get("sex")) == 2 ? 0 : 1);
            weChatResponse.setHeadimgurl(ConvertUtils.toString(result.get("headimgurl")));
            weChatResponse.setUnionid(ConvertUtils.toString(result.get("unionid")));
            weChatResponse.setContry(ConvertUtils.toString(result.get("contry")));
            weChatResponse.setProvince(ConvertUtils.toString(result.get("province")));
            weChatResponse.setCity(ConvertUtils.toString(result.get("city")));
        }

        return weChatResponse;
    }

    /**获取accessToken
     *
     * @return
     */
    private String getAccessToken() {

        String key = "oauth2_access_token";
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
                //已过去时间
                Integer cacheTime = ConvertUtils.toInteger(date.getTime() - weChatSource.getRefreshTime() / 1000);
                if (cacheTime >= weChatSource.getExpireIn()) {

                    String url = String.format(
                            "%s?appid=%s&grant_type=refresh_token&refresh_token=%s",
                            REFRESH_TOKEN_URL, oAuthCofnig.getAppId(), weChatSource.getRefreshToken());

                    Map<String, Object> result = this.get(url);
                    if (result != null) {
                        weChatSource.setAccessToken(ConvertUtils.toString(result.get("access_token")));
                        //过期时间，通常为7200
                        weChatSource.setExpireIn(7000);
                        //刷新的token，有效期为30天
                        weChatSource.setRefreshToken(ConvertUtils.toString(result.get("refresh_token")));
                        //刷新时间
                        weChatSource.setRefreshTime(date.getTime());

                        jedis.set(key.getBytes(), SerializationUtils.serialize(weChatSource));
                    }
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

        String key = "oauth2_access_token";
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

    private Map<String, Object> get(String url) {

        if (StringUtils.isEmpty(url)) return null;

        HttpClient hc = new HttpClient(url,10000,30000);
        try {
            int status = hc.sendGet("UTF-8");
            if (200 == status) {
                String resultString = hc.getResult();
                if (!StringUtils.isEmpty(resultString)) {
                    Map<String, Object> result = JsonConverter.toObject(resultString, Map.class);
                    //请求出错，记录错误信息
                    if (result.containsKey("errcode"))
                        Constants.logger.error("code:" + result.get("errcode") + ", message:" + result.get("errmsg"));

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
