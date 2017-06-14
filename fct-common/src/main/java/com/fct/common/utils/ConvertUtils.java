package com.fct.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * Created by jon on 2017/6/9.
 */
public class ConvertUtils {

    public static String toString(Object value)
    {
        if(value == null)
        {
            return "";
        }
        return value.toString();
    }

    public static Integer toInteger(Object value,Integer defaultValue)
    {
        if(value == null || StringUtils.isEmpty(value.toString()))
        {
            return defaultValue;
        }
        return Integer.valueOf(value.toString());
    }

    public static Integer toInteger(Object value)
    {
        return toInteger(value,0);
    }

    public static BigDecimal toBigDeciaml(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return new BigDecimal(0);
        }
        return new BigDecimal(value.toString());
    }

    public static Double toDouble(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return new Double(0);
        }
        return Double.valueOf(value.toString());
    }

    public static Float toFloat(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return new Float(0);
        }
        return Float.valueOf(value.toString());
    }

    public static Boolean toBoolean(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return false;
        }
        return Boolean.valueOf(value.toString());
    }

    public static Integer toPageIndex(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return 1;
        }
        return Integer.valueOf(value.toString());
    }


    public static String[] toStringArray(Object value)
    {
        if(value == null ||StringUtils.isEmpty(value.toString()))
        {
            return new String[]{};
        }
        return value.toString().split(",");
    }
}
