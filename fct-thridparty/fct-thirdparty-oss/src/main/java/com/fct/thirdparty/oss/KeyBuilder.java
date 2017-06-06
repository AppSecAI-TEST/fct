package com.fct.thirdparty.oss;

import java.util.Date;

/**
 * Created by ningyang on 2017/6/6.
 */
public class KeyBuilder {

    private static final String PREFIX = "fct/";

    public static String buildKey(String fileName){
        String suffix = fileName.split("\\.")[1];
        StringBuffer str = new StringBuffer();
        str.append(PREFIX).append(new Date().getTime()).append(".").append(suffix);
        return str.toString();
    }
}
