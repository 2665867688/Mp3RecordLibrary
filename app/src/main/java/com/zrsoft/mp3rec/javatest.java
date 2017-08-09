package com.zrsoft.mp3rec;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class javatest {
    public static void main(String[] args) {
        long ms = 300000;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        System.out.println(hms);
    }
}
