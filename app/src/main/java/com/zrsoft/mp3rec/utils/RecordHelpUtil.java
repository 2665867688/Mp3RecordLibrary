package com.zrsoft.mp3rec.utils;

import android.os.Environment;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class RecordHelpUtil {

    private static String TAG = RecordHelpUtil.class.getName();

    /**
     * SD卡是否正常
     * @return
     */
    public  static boolean isStorageAvailable() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v(TAG, "SD卡不可用");
            return false;
        }
        return true;
    }


    public static String getSDPath(){
        if(isStorageAvailable()){

            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        }else{
            return null;
        }
    }

    public static String misToTime(long mis){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(mis);
        return hms;
    }
}
