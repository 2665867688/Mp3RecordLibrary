package com.zrsoft.mp3rec.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class Utils {

    /**
     * 调用系统播放器播放音频
     * @param position
     */
    public static void startAudio( Activity context, Uri uri) {
        Intent mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_VIEW);
        mIntent.setDataAndType(uri, "audio/mp3");
        context.startActivity(mIntent);
    }
}
