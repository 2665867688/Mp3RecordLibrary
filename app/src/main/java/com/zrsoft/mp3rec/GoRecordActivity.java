package com.zrsoft.mp3rec;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @ClassName:GoRecordActivity
 * @author: shimy
 * @date: 2017/8/8 0008 下午 3:47
 * @description: 录音
 */
public class GoRecordActivity extends AppCompatActivity {

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_record);
        uri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}
