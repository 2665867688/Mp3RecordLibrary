package com.zrsoft.mp3rec;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zrsoft.mp3rec.lame.MP3Recorder;
import com.zrsoft.mp3rec.utils.RecorderAndPlayUtil;

import java.util.ArrayList;

/**
 * @ClassName:GoRecordActivity
 * @author: shimy
 * @date: 2017/8/8 0008 下午 3:47
 * @description: 录音
 */
public class GoRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnControl;
    private Button btnSave;
    private Button btnGiveUp;
    private Uri uri;
    private RecorderAndPlayUtil mRecorder = null;
    private int state = 0;//0：开始 1：播放中 2：暂停
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_record);
        mRecorder = new RecorderAndPlayUtil(this,uri);
        mRecorder.getRecorder().setHandle(handler);

        uri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        btnControl = (Button) findViewById(R.id.btn_control);
        btnControl.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        btnGiveUp = (Button) findViewById(R.id.btn_give_up);
        btnGiveUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_control) {
            if (state ==0){
                mRecorder.startRecording();
                btnControl.setText("暂停");
                state = 1;
            }else if (state == 1){
                mRecorder.pauseRecording();
                btnControl.setText("继续");
                state = 2;
            }else if (state ==2){
                mRecorder.restoreRecording();
                btnControl.setText("暂停");
                state = 1;
            }
        } else if (i == R.id.btn_save) {

        } else if (i == R.id.btn_give_up){
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MP3Recorder.MSG_REC_STARTED:
                    // 开始录音
                    Toast.makeText(getApplicationContext(), "开始录音",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_REC_STOPPED:
                    //停止录音 录音保存
                    Toast.makeText(getApplicationContext(), "停止录音",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_GET_MIN_BUFFERSIZE:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "采样率手机不支持",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_CREATE_FILE:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "创建音频文件出错",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_REC_START:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "初始化录音器出错",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_AUDIO_RECORD:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "录音的时候出错",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_AUDIO_ENCODE:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "编码出错",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_WRITE_FILE:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "文件写入出错",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MP3Recorder.MSG_ERROR_CLOSE_FILE:
                    initRecording();
                    Toast.makeText(getApplicationContext(), "文件流关闭出错",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void initRecording() {
        mRecorder.stopRecording();
    }
}
