package com.wz.pilipili.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    public enum TimeUnit {
        MINUTE, HOUR, DAY, MONTH, YEAR
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 将Date类型格式化
     */
    public static String dateFormat(Date date) {
        // 将 Date 类型转换为指定格式的字符串
        return dateFormat.format(date);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss的字符串转换成Date类型
     */
    public static Date convertStringToDate(String dateTimeStr) throws ParseException {
        return dateFormat.parse(dateTimeStr);
    }


    /**
     * 两个String类型的日期进行比较，仅比较到day
     */
    public static boolean dateCompareByDay(String dateTimeStr1,String dateTimeStr2) {
        // 解析字符串为 LocalDateTime
        LocalDateTime dateTime1 = LocalDateTime.parse(dateTimeStr1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeStr2, formatter);
        // 提取 LocalDate 部分
        LocalDate date1 = dateTime1.toLocalDate();
        LocalDate date2 = dateTime2.toLocalDate();
        // 比较 LocalDate
        return date1.equals(date2);
    }


    public static String minus(Date date, int amount, TimeUnit timeUnit) {
        // 创建一个 Calendar 实例并设置为指定日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 根据指定的时间单位减去相应的时间量
        switch (timeUnit) {
            case MINUTE:
                calendar.add(Calendar.MINUTE, -amount);
                break;
            case HOUR:
                calendar.add(Calendar.HOUR, -amount);
                break;
            case DAY:
                calendar.add(Calendar.DAY_OF_MONTH, -amount);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, -amount);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, -amount);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + timeUnit);
        }
        // 定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 返回格式化后的日期字符串
        return dateFormat.format(calendar.getTime());
    }
}
