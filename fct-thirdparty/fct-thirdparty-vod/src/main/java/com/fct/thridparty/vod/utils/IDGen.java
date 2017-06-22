package com.fct.thridparty.vod.utils;

import java.util.UUID;

/**
 * Created by nick on 2017/5/19.
 * 创建随机数
 */
public class IDGen {

    public static String gen(){
        return UUID.randomUUID().toString();
    }
}
