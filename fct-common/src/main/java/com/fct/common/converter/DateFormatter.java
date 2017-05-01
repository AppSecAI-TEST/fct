package com.fct.common.converter;



import com.fct.common.exceptions.Exceptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ningyang
 */
public class DateFormatter {

    private static final String standardDatePattern = "yyyy-MM-dd";
    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
    private static final String appointmentPattern = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final String appointmentMonthDayPattern = "MM月dd日";
    private static final String appointmentTimePattern = "HH:mm";

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat df = createFromPattern(pattern);
        return df.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null) {
            return null;
        }
        DateFormat df = createFromPattern(pattern);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw Exceptions.unchecked(e);
        }
    }

    private static DateFormat createFromPattern(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static String dateFormat(Date date) {
        return format(date, standardDatePattern);
    }

    public static Date parseDate(String dateStr) {
        return parse(dateStr, standardDatePattern);
    }

    public static String dateTimeFormat(Date date) {
        return format(date, dateTimePattern);
    }

    public static Date parseDateTime(String dateStr) {
        return parse(dateStr, dateTimePattern);
    }

    public static String appointmentDateTime(Date date) {
        return format(date, appointmentPattern);
    }

    public static String appointmentMonthDay(Date date) {
        return format(date, appointmentMonthDayPattern);
    }

    public static String appointmentTime(Date date) {
        return format(date, appointmentTimePattern);
    }
}
