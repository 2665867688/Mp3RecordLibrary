package com.zrsoft.mp3rec.utils;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zrsoft.mp3rec.R;

/**
 *
 * @ClassName:OutCallRecordActivity
 * @author: shimy
 * @date: 2017/8/9 0009 上午 11:23
 * @description: 供外部调用的录音
 */
public class OutCallRecordActivity extends AppCompatActivity {

    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_call_record);
        uri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }
}
