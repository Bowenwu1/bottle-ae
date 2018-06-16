package com.pear.bottle_ae;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;

/**
 * Created by zhuojun on 2018/06/12.
 */

public class TimeResolver {
    public static String getRelativeTime(String time) {
        String relativeTime;
        // set format
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone timeZone = TimeZone.getTimeZone("GMT+00:00");
        pattern.setTimeZone(timeZone);
        Date date = null;
        // parse time string
        try {
            date = pattern.parse(time);
            System.out.print(date.getTime());
        } catch (Exception e) {
            System.out.print(e);
        }

        long passTime =  (new Date().getTime() - date.getTime());

        int days = (int) (passTime/(1000 * 60 * 60 * 24));
        int mins = (int) (passTime/(1000 * 60) - (days * 60 * 24));

        // different display mode
        if (days > 0) {
            if (days >= 365) {
                relativeTime = Integer.toString(days/365) + "年前";
            } else {
                if (days >= 30) {
                    relativeTime = Integer.toString(days/30) + "个月前";
                } else {
                    relativeTime = Integer.toString(days) + "天前";
                }
            }
        } else {
            if (mins >= 60) {
                relativeTime = Integer.toString(mins/60) + "小时前";
            } else {
                relativeTime = Integer.toString(mins) + "分钟前";
            }
        }

        return relativeTime;
    }
}
