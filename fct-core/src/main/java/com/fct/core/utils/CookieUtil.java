package com.fct.core.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {

    /**
     * 添加cookie
     * @param response
     * @param name
     * @param value
     */
    public static void addCookie(HttpServletResponse response,String name,String value,String domain){
        Cookie cookie = new Cookie(name.trim(), value.trim());
        cookie.setMaxAge(30 * 60);// 设置为30min,浏览器关闭cookie失效
        cookie.setPath("/");
        if(!StringUtils.isEmpty(domain))
        {
            cookie.setDomain(domain);
        }
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletRequest request,HttpServletResponse response,String name,String value){
        addCookie(response,name,value,HttpUtils.getHost(request));
    }
    /**
     * 修改cookie
     * @param request
     * @param response
     * @param name
     * @param value
     * 注意一、修改、删除Cookie时，新建的Cookie除value、maxAge之外的所有属性，例如name、path、domain等，都要与原Cookie完全一样。否则，浏览器将视为两个不同的Cookie不予覆盖，导致修改、删除失败。
     */
    public static void editCookie(HttpServletRequest request,HttpServletResponse response,String name,String value,
                                  String domain){
        Cookie[] cookies = request.getCookies();
        if (null==cookies) {
            System.out.println("没有cookie==============");
        } else {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    System.out.println("原值为:"+cookie.getValue());
                    cookie.setValue(value);
                    cookie.setPath("/");
                    if(!StringUtils.isEmpty(domain))
                    {
                        cookie.setDomain(domain);
                    }
                    cookie.setMaxAge(30 * 60);// 设置为30min
                    System.out.println("被修改的cookie名字为:"+cookie.getName()+",新值为:"+cookie.getValue());
                    response.addCookie(cookie);
                    break;
                }
            }
        }

    }
    public static void editCookie(HttpServletRequest request,HttpServletResponse response,String name,String value)
    {
        editCookie(request,response,name,value,HttpUtils.getHost(request));
    }
    /**
     * 删除cookie
     * @param request
     * @param response
     * @param name
     */
    public static void delCookie(HttpServletRequest request,HttpServletResponse response,String name,String domain){
        Cookie[] cookies = request.getCookies();
        if (null!=cookies) {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    if(!StringUtils.isEmpty(domain))
                    {
                        cookie.setDomain(domain);
                    }
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
    public static void delCookie(HttpServletRequest request,HttpServletResponse response,String name)
    {
        delCookie(request,response,name,HttpUtils.getHost(request));
    }

    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    public static String getCookieByName(HttpServletRequest request,String name){
        Map<String,Cookie> cookieMap = ReadCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie.getValue();
        }else{
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     * @param request
     * @return
     */
    private  static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
