package com.fct.common.converter;

/**
 * Created by limenghua on 2016/8/16.
 *
 * @author limenghua
 */
public class NumberFormatter {

    public static String numToStr(int num) {
        String result = String.valueOf(num);
        if (num == 10000) {
            result = "1万";
        } else if (num > 10000) {
            int wan = num / 10000;
            int kilo = num % 10000 / 1000;
            if (kilo == 0) {
                return String.format("%s万", wan);
            } else {
                return String.format("%s.%s万", wan, kilo);
            }
        }
        return result;
    }

    public static void main(String args[]) {
        int num = 212989;
        System.out.println(numToStr(num));
    }
}
