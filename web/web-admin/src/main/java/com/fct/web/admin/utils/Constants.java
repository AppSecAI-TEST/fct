package com.fct.web.admin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public Constants()
    {

    }

    public static final Logger logger = LoggerFactory.getLogger("EX");

    public String filePath(String filename)
    {
        if(StringUtils.isEmpty(filename))
        {
            return "";
        }

        return "http://fct-nick.img-cn-shanghai.aliyuncs.com"+filename;
    }

    public String thumbnailPath(String imgsrc)
    {
        if(StringUtils.isEmpty(imgsrc))
        {
            return "";
        }

        return "http://fct-nick.img-cn-shanghai.aliyuncs.com"+imgsrc+"@200w.jpg";
    }

    public String uploadPath(String filename)
    {
        if(StringUtils.isEmpty(filename))
        {
            return "";
        }
        return "http://fct-nick.img-cn-shanghai.aliyuncs.com"+filename;
    }
}
