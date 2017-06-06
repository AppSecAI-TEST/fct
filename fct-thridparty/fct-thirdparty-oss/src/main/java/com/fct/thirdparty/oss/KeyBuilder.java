package com.fct.thirdparty.oss;

import java.util.Date;

/**
 * Created by ningyang on 2017/6/6.
 */
public class KeyBuilder {

    private static final String PREFIX = "upload/";

    public static String buildKey(String fileName){

        StringBuffer str = new StringBuffer();
        str.append(PREFIX).append(new Date().getTime()).append("/").append(fileName);
        return str.toString();
    }
}
