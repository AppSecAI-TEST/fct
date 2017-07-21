package com.fct.pay.service.alipay;

/**
 * Created by jon on 2017/7/21.
 */
public class SDKConfig {
    // 商户appid
    private static String APPID = "";
    // 私钥 pkcs8格式的
    private static String RSA_PRIVATE_KEY = "";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    private static String notify_url = "";

    private static String app_notify_url = "";

    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    private static String return_url = "";
    // 请求网关地址
    private static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    private static String CHARSET = "UTF-8";
    // 返回格式
    private static String FORMAT = "json";
    // 支付宝公钥
    private static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjrEVFMOSiNJXaRNKicQuQdsREraftDA9Tua3WNZwcpeXeh8Wrt+V9JilLqSa7N7sVqwpvv8zWChgXhX/A96hEg97Oxe6GKUmzaZRNh0cZZ88vpkn5tlgL4mH/dhSr3Ip00kvM4rHq9PwuT4k7z1DpZAf1eghK8Q5BgxL88d0X07m9X96Ijd0yMkXArzD7jg+noqfbztEKoH3kPMRJC2w4ByVdweWUT2PwrlATpZZtYLmtDvUKG/sOkNAIKEMg3Rut1oKWpjyYanzDgS7Cg3awr1KPTl9rHCazk15aNYowmYtVabKwbGVToCAGK+qQ1gT3ELhkGnf3+h53fukNqRH+wIDAQAB";
    // 日志记录目录
    private static String log_path = "/log";
    // RSA2
    private static String SIGNTYPE = "RSA2";

    public static String getAPPID()
    {
        return SDKConfig.APPID;
    }

    public static void setAPPID(String appid)
    {
        SDKConfig.APPID = appid;
    }

    public static String getRsaPrivateKey()
    {
        return SDKConfig.RSA_PRIVATE_KEY;
    }

    public static void setRsaPrivateKey(String rsaPrivateKey)
    {
        SDKConfig.RSA_PRIVATE_KEY = rsaPrivateKey;
    }

    public static String getNotify_url()
    {
        return SDKConfig.notify_url;
    }

    public static void setNotify_url(String notify_url)
    {
        SDKConfig.notify_url = notify_url;
    }

    public static String getApp_notify_url()
    {
        return SDKConfig.app_notify_url;
    }

    public static void setApp_notify_url(String app_notify_url)
    {
        SDKConfig.app_notify_url = app_notify_url;
    }

    public static String getReturn_url()
    {
        return SDKConfig.return_url;
    }

    public static void setReturn_url(String return_url)
    {
        SDKConfig.return_url = return_url;
    }

    public static String getURL()
    {
        return SDKConfig.URL;
    }

    public static void setURL(String url)
    {
        SDKConfig.URL = url;
    }

    public static String getCHARSET()
    {
        return SDKConfig.CHARSET;
    }

    public static void setCHARSET(String charset)
    {
        SDKConfig.CHARSET = charset;
    }

    public static String getFORMAT()
    {
        return SDKConfig.FORMAT;
    }

    public static void setFORMAT(String format)
    {
        SDKConfig.FORMAT = format;
    }

    public static String getAlipayPublicKey()
    {
        return SDKConfig.ALIPAY_PUBLIC_KEY;
    }

    public static void setAlipayPublicKey(String alipayPublicKey)
    {
        SDKConfig.ALIPAY_PUBLIC_KEY = alipayPublicKey;
    }

    public static String getLog_path()
    {
        return SDKConfig.log_path;
    }

    public static void setLog_path(String log_path)
    {
        SDKConfig.log_path = log_path;
    }

    public static String getSIGNTYPE()
    {
        return SDKConfig.SIGNTYPE;
    }

    public static void setSIGNTYPE(String signtype)
    {
        SDKConfig.SIGNTYPE = signtype;
    }

}
