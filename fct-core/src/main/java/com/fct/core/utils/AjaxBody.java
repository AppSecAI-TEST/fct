package com.fct.core.utils;

import java.util.Map;

/**
 * Created by jon on 2017/6/4.
 */
public class AjaxBody {

    protected String method="";

    protected String message ="";

    protected String func ="";

    protected String url="";

    protected Map<String,String> dic =null;

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getMethod()
    {
        return this.method;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage(){return this.message;}

    public void setFunc(String func)
    {
        this.func = func;
    }

    public String getFunc(){return this.func;}

    public void setDic(Map<String,String> dic)
    {
        this.dic = dic;
    }

    public Map<String,String> getDic(){return this.dic;};

}
