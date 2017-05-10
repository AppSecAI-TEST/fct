package com.fct.pay.service.wxpay.common;

import java.util.Random;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:18
 */
public class RandomStringGenerator {

    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成时间戳，标准北京时间，时区为东八区，自1970年1月1日 0点0分0秒以来的秒数
     * @return 时间戳
     */
    public static String getGenerateTimeStamp()
    {
        return String.valueOf(System.currentTimeMillis());
    }

}
