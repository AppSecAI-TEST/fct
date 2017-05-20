package com.fct.thridparty.vod.utils;

import com.fct.thridparty.vod.handler.VodHandler;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.dsig.SignatureMethod;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/19.
 * 签名生成器
 */
public class SignatureGenerator {

    private static final String ENCODE_TYPE = "UTF-8";
    private static final String HTTP_METHOD = "GET";
    private static final String SEPARATOR = "&";
    private static final String EQUAL = "=";
    private static final String ALGORITHM = "HmacSHA1";

    public static String generator(VodHandler handler, String accessKeySecret){
        return getSignature(handler.getAllParam(), accessKeySecret);
    }

    private static String getSignature(Map<String, Object> param, String accessKeySecret){
        if(param==null)
            throw new IllegalArgumentException("请填充请求参数否则无法签名");

        try{
            List<String> sortedKeys = new ArrayList<>(param.keySet());
            Collections.sort(sortedKeys);
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append(HTTP_METHOD).append(SEPARATOR);
            stringToSign.append(percentEncode("/")).append(SEPARATOR);
            StringBuilder canonicalizedQueryString = new StringBuilder();
            for (String key : sortedKeys) {
                // 此处需要对key和value进行编码
                String value = (String)param.get(key);
                canonicalizedQueryString.append(SEPARATOR).append(percentEncode(key)).append(EQUAL).append(percentEncode(value));
            }
            // 此处需要对canonicalizedQueryString进行编码
            stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
            SecretKey key = new SecretKeySpec((accessKeySecret + SEPARATOR).getBytes(ENCODE_TYPE), SignatureMethod.HMAC_SHA1);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            String signature = URLEncoder.encode(new String(new Base64().encode(mac.doFinal(stringToSign.toString().getBytes(ENCODE_TYPE))),
                    ENCODE_TYPE), ENCODE_TYPE);
            return signature;
        }catch  (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String percentEncode(String value) throws UnsupportedEncodingException {
        if (value == null) return null;
        return URLEncoder.encode(value, ENCODE_TYPE).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

}
