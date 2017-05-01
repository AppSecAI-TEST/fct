package com.fct.common.utils;

/**
 * @author ningyang
 *
 */
public class ArraysUtil {


    /**
     * 分割数组 list to string
     */
    public static String split2Sting(Iterable<String> list, String separator){
        if (list == null) {
            return "";
        }
        String rt = "";
        for (String str : list) {
            rt += separator + str;
        }
        return rt.length() > 0 ? rt.substring(separator.length()) : "";
    }

}
