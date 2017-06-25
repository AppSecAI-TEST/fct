package com.fct.core.utils;

import java.util.Random;

/**
 * @author ningyang
 */
public class ErrorMessageSelector {

    private static final String[] messagesPool = new String[]{
            "网络实在太差, 请稍后再试",
            "您的网速实在太慢了, 请稍后重试",
    };

    private static final Random r = new Random();
    private static final int randomMax = messagesPool.length;

    public static String getOne() {
        return messagesPool[r.nextInt(randomMax)];
    }
}
