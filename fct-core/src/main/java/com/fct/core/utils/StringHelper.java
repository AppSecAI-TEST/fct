package com.fct.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

/**
 * Created by jon on 2017/5/5.
 */
public class StringHelper {

    /**利用MD5进行加密
     * @param str  待加密的字符串
     * @return  加密后的字符串
     * @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public static String md5(String str){

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
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
     * @作者 尧
     * @功能 String左对齐
     */
    public static String padLeft(String src, int len, String add) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src.substring(0,len);
        }

        for(int i=0;i<diff;i++)
        {
            add += add;
        }
        return src+add;
    }
    /**
     * @作者 尧
     * @功能 String右对齐
     */
    public static String padRight(String src, int len, String add) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src.substring(0,len);
        }

        for(int i=0;i<diff;i++)
        {
            add += add;
        }
        return add+src;
    }

    public static String generateOrderId()
    {
        Date time = DateUtils.parseString(DateUtils.getTodayBegin(),"yyyy-MM-dd");
        Long seconds = DateUtils.compareDate(new Date(),time);
        return DateUtils.getNowDateStr("yyMMdd")+padLeft(seconds.toString(),5,"0")+
                getRandomNumber(5);
    }

    public static String generatePayOrderId()
    {
        Date time = DateUtils.parseString(DateUtils.getTodayBegin(),"yyyy-MM-dd");
        Long seconds = DateUtils.compareDate(new Date(),time);
        return padLeft(seconds.toString(),5,"1")+DateUtils.getNowDateStr("yyMMdd")+
                getRandomNumber(5);
    }

    public static String getRandomNumber(int length)
    {
        String bound = "";
        for(int i=0;i<length;i++)
        {
            bound +="9";
        }

        Integer ranCode = new Random().nextInt(Integer.valueOf(bound));

        return String.format("%0"+length+"d", ranCode);
    }
}
