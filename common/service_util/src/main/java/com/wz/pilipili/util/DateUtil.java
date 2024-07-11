package com.wz.pilipili.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    public enum TimeUnit {
        MINUTE, HOUR, DAY, MONTH, YEAR
    }

    /**
     * 将Date类型格式化
     */
    public static String dateFormat(Date date) {
        // 定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 将 Date 类型转换为指定格式的字符串
        return dateFormat.format(date);
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
