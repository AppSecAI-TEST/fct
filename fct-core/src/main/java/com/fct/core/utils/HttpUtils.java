package com.fct.core.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static final String domain = "fangcun.com";

    public static final String url = "http://www.fangcun.com";

    public static final String payUrl = "http://pay.fangcun.com";

    public static final String adminUrl = "http://admin.fangcun.com";

    public static String staticUrl(String path)
    {
        if(StringUtils.isEmpty(path))
        {
            return "";
        }

        return "http://static.fangcun.com"+path;
    }

    public static String imagesUrl(String path)
    {
        if(StringUtils.isEmpty(path))
        {
            return "";
        }

        return "http://img.fangcun.com"+path;
    }

    public static String videoUrl(String path)
    {
        if(StringUtils.isEmpty(path))
        {
            return "";
        }

        return "http://video.fangcun.com"+path;
    }

    public static String thumbnail(String path,Integer width)
    {
        if(StringUtils.isEmpty(path))
        {
            return "";
        }

        if(width<=0)
        {
            width =120;
        }

        return String.format("http://img.fangcun.com%s@%dw.jpg",path,width);
    }

    public static String getHost(HttpServletRequest request)
    {
        try {
            URL url = new URL(request.getRequestURL().toString());

            String host = url.getHost();// 获取主机名

            host = host.substring(host.indexOf('.')+1);

            return host;
        }
        catch (MalformedURLException exp)
        {

        }
        return "";
    }
}
