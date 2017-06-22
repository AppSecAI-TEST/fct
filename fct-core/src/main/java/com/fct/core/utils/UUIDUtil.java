package com.fct.core.utils;

import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil(){}

    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args){
        System.out.println("uuid " + generateUUID());
    }
}
