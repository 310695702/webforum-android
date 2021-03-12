package com.kcbs.webforum.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static long timeToStamp(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return timeStemp;
    }


    public static String calculate(long time) {
        long timeInMillis = Calendar.getInstance().getTimeInMillis();

        //兼容脏数据。抓取的数据有些帖子的时间戳不是标准的十三位
        String valueOf = String.valueOf(time);
        if (valueOf.length() < 13) {
            time = time * 1000;
        }
        long diff = (timeInMillis - time) / 1000;
        if (diff <= 5) {
            return "刚刚";
        } else if (diff < 60) {
            return diff + "秒前";
        } else if (diff < 3600) {
            return diff / 60 + "分钟前";
        } else if (diff < 3600 * 24) {
            return diff / (3600) + "小时前";
        } else if (diff < (3600 * 24 * 365)){
            return diff / (3600 * 24) + "天前";
        }else {
            return diff / (3600 * 24 * 365) + "年前";
        }
    }

    public static String  timeTocalculate(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return TimeUtils.calculate(timeStemp);
    }
}
