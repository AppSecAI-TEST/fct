package com.fct.common.service.oss;

import com.fct.core.utils.UUIDUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ningyang on 2017/6/6.
 */
public class KeyBuilder {

    private static final String PREFIX = "upload/";
    private static SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

    public static String buildKey(String fileName){
        String suffix = fileName.split("\\.")[1];
        StringBuffer str = new StringBuffer();
        str.append(PREFIX).append(format.format(new Date())).append("/").append(UUIDUtil.generateUUID()).
                append(".").append(suffix);
        return str.toString();
    }
}
