package com.fct.common.utils;


import org.joda.time.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ningyang
 *
 */
public class DateUtils {

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdf_day_hour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat sdf_hour = new SimpleDateFormat("HH:mm");
    public final static String YMD = "yyyy-MM-dd";

    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 获取中国日历
     * @param date
     * @return
     */
    public static String getChineseDate(Date date){
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int days = calendar.get(Calendar.DAY_OF_MONTH);
        builder.append(year).append("年");
        builder.append(month).append("月");
        builder.append(days).append("日");
        return builder.toString();
    }
    /**
     * 根据日期返回时上午还是下午
     * @param date
     * @return
     */
    public static String getAPM(Date date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour<12){
            str = "上午";
        }else{
            str = "下午";
        }
        return str;
    }

    /**
     * 获取n周后的一天
     * @param date
     * @param n
     * @return
     */
    public static Date addWeekDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        calendar.add(calendar.DATE,7*n);
        Date result = calendar.getTime();
        return result;
    }


    /**
     * 根据生日计算年龄
     * @param date
     * @return
     */
    public static String caculateAge(Date date){
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(date);
        Calendar nowCalendar = Calendar.getInstance();
        // 从生日到当前日期，相差多少个月
        int diffMonth = (nowCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)) * 12 +
                nowCalendar.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH);
        if (diffMonth < 0){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int diffYear = diffMonth/12;
        diffMonth = diffMonth%12;
        if(diffYear > 0){
            stringBuilder.append(diffYear).append("岁");
        }

        if(diffMonth > 0){
            stringBuilder.append(diffMonth).append("个月");
        }

        if(diffYear == 0 && diffMonth == 0){
            int diffDay = nowCalendar.get(Calendar.DAY_OF_MONTH) - birthCalendar.get(Calendar.DAY_OF_MONTH);
            if (diffDay > 0){
                stringBuilder.append(diffDay).append("天");
            }
        }
        String str = stringBuilder.toString();
        if(str.length()==0){
            str = "刚出生";
        }
        return str;
    }

    /**
     * 获取n月后的一天
     * @param date
     * @param n
     * @return
     */
    public static Date addMonthDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        calendar.add(calendar.MONTH,n);
        Date result = calendar.getTime();
        return result;
    }

    /**
     * 比较两个日期的大小
     * @param date1
     * @param date2
     * @return 负数表示 date1在date2之前
     */
    public static long compareDate(Date date1, Date date2) {
        long date1l = date1.getTime();
        long date2l = date2.getTime();

        return (date1l-date2l);
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static int daysBetween(Date smdate,Date bdate){
        String small = sdf_day.format(smdate);
        String future = sdf_day.format(bdate);
        DateTime smDateTime = new DateTime(small);
        DateTime bigDateTime = new DateTime(future);
        int days = Days.daysBetween(smDateTime, bigDateTime).getDays();
        Minutes.minutesBetween(smDateTime,bigDateTime).getMinutes();
        return days;
    }

    public static int minutesBetween(Date smdate,Date bdate){
        String small = sdf_day.format(smdate);
        String future = sdf_day.format(bdate);
        DateTime smDateTime = new DateTime(small);
        DateTime bigDateTime = new DateTime(future);
        int minutes = Minutes.minutesBetween(smDateTime,bigDateTime).getMinutes();
        return minutes;
    }

    public static int hourBetween(Date smdate,Date bdate){
        String small = sdf_day.format(smdate);
        String future = sdf_day.format(bdate);
        DateTime smDateTime = new DateTime(small);
        DateTime bigDateTime = new DateTime(future);
        int hour = Hours.hoursBetween(smDateTime,bigDateTime).getHours();
        return hour;
    }

    public static int MonthsBetween(Date smdate, Date bdate){
        String small = sdf_day.format(smdate);
        String future = sdf_day.format(bdate);
        DateTime smDateTime = new DateTime(small);
        DateTime bigDateTime = new DateTime(future);
        return Months.monthsBetween(smDateTime, bigDateTime).getMonths();
    }

    public static Date addMinute(Date date, int minute){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusMinutes(minute);
        return  dateTime.toDate();
    }

    public static Date addHour(Date date, int hour){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusHours(hour);
        return  dateTime.toDate();
    }

    public static Date addDay(Date date, int day){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusDays(day);
        return  dateTime.toDate();
    }

    public static Date addMonth(Date date, int month){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusMonths(month);
        return  dateTime.toDate();
    }

    public static Date addYear(Date date, int year) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusYears(year);
        return dateTime.toDate();
    }


    public static String getMessageTime(Date msgTime){
        StringBuilder builder = new StringBuilder();
        int daysBetween = daysBetween(msgTime, new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(msgTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);
        String convMins = null;
        if(mins<10){
            convMins = "0" + String.valueOf(mins);
        }else{
            convMins = String.valueOf(mins);
        }
        if(daysBetween < 1){
            builder.append("今天 ").append(hour).append(":").append(convMins);
        }else if(daysBetween == 1){
            builder.append("昨天 ").append(hour).append(":").append(convMins);
        }else if(daysBetween == 2){
            builder.append("前天 ").append(hour).append(":").append(convMins);
        }else{
            String time = sdf_day.format(msgTime);
            builder.append(time + " ");
        }
        return builder.toString();
    }

    public static Date parseString(String str){
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date parseString(String str,String format){
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTodayBegin(){
        return sdf_day.format(new Date()) + " 00:00:00";
    }

    public static String format2day(Date date){
        return sdf_day.format(date);
    }

    /**
     * 转换date to string
     * 一分钟内转为
     * @param date
     * @return
     */
    public static String formatDate2Custom(Date date){
        int defTime = (int) ((new Date().getTime() - date.getTime())/1000);
        if (defTime < 0){
            DateTime dateTime = new DateTime(date);
            return dateTime.toString("yyyy-MM-dd HH:mm:ss");
        }
        String create_day = new DateTime(date).toString("yyyy-MM-dd");
        if (defTime < 60) {
            return "刚刚";
        }else if (defTime < 3600){
            return defTime/60+"分钟前";
        }else if (defTime < 86400){
            return defTime/3600+"小时前";
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String last_1_day = new DateTime(calendar.getTime()).toString("yyyy-MM-dd");
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String last_2_day = new DateTime(calendar.getTime()).toString("yyyy-MM-dd");
            if (last_1_day.equals(create_day)){
                return "昨天" + new DateTime(date).toString("HH:mm");
            }else if (last_2_day.equals(create_day)){
                return "前天" + new DateTime(date).toString("HH:mm");
            }
        }
        return new DateTime(date).toString("yyyy-MM-dd HH:mm");
    }

    /**
     * 根据给定格式格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
    /**
     * 根据指定格式获取 当前时间 字符串
     *
     * @param format
     * @return
     */
    public static String getNowDateStr(String format) {
        String nowDateStr = formatDate(new Date(), format);
        return nowDateStr;
    }

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date testDate = sdf.parse("2016-10-19 16:37:37");

        String show = DateUtils.formatDate2Custom(testDate);
        System.out.println(show);
    }
}
