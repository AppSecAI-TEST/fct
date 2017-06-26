package com.fct.web.admin.utils;

import com.fct.core.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public Constants()
    {

    }

    public static final Logger logger = LoggerFactory.getLogger("EX");

    public String staticFile(String path)
    {
        return HttpUtils.staticFile(path);
    }

    public String thumbnail(String path)
    {
        return HttpUtils.thumbnail(path);
    }

    public String uploadFile(String path)
    {
        return HttpUtils.uploadFile(path);
    }
}
