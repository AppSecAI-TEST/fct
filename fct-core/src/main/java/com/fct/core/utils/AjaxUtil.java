package com.fct.core.utils;

import com.fct.core.json.JsonConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by jon on 2017/6/4.
 */
public class AjaxUtil {
    /// <summary>
    /// 提交返回信息
    /// <param name="sMessage"></param>
    /// </summary>
    public static String alert(String message)
    {
        return respondJson("alert", message, "","", null);
    }

    /// <summary>
    /// 提交返回信息,跳转到指定页
    /// <param name="sMessage">提示的信息</param>
    /// <param name="sURL">跳转的页面</param>
    /// </summary>
    public static String goUrl(String url,String message)
    {
        return respondJson("goto", message, url, "", null);
    }

    /// <summary>
    /// 提交返回信息,刷新本页
    /// <param name="sMessage">提示信息</param>
    /// </summary>
    public static String reload(String message)
    {
        return respondJson("reload", message,"","", null);

    }
    public static String remind(String message)
    {
        return respondJson("remind", message, "", "", null);
    }
    /// <summary>
    /// 提交后,用户自定义js方法
    /// <param name="function">传入自定义的内容</param>
    /// </summary>
    public static String eval(String function)
    {
        return respondJson("function", "", "",function, null);
    }

    /// <summary>
    /// 提交多个错误信息
    /// </summary>
    public static String error(Map<String, String> dic)
    {
        return respondJson("error", "", "","", dic);
    }

     //将字符窜转换为json格式

    static String respondJson(String method, String message, String url,String function,Map<String, String> error) {

        AjaxBody body = new AjaxBody();
        body.setMethod(method);
        if (!StringUtils.isEmpty(url)) {
            body.setUrl(url);
        }
        if (!StringUtils.isEmpty(message)) {
            body.setMessage(message);
        }
        if (!StringUtils.isEmpty(function)) {
            body.setFunc(function);
        }
        if (error != null && error.size() > 0) {
            body.setDic(error);
        }

        return JsonConverter.toJson(body);
    }
}

