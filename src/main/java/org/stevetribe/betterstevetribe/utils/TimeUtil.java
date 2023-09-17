package org.stevetribe.betterstevetribe.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static long string2timestamp(String date) {
        SimpleDateFormat f = new SimpleDateFormat("YYYY-MM-dd");
        try {
            Date d = f.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static String timestamp2string(long timestamp) {
        Date date=new Date(timestamp);
        SimpleDateFormat f = new SimpleDateFormat("YYYY-MM-dd");
        return f.format(date);
    }
}
