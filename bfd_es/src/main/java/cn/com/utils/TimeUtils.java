package cn.com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BFD_278 on 2015/7/21.
 */
public class TimeUtils {

    public static String timestamp2Date(long timestamp){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = timeFormat.format(new Date(timestamp * 1000L));
        return date;
    }
}
