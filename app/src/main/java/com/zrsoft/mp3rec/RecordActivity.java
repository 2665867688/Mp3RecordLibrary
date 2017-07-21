package com.zrsoft.mp3rec;

import java.io.File;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.zrsoft.mp3rec.lame.MP3Recorder;
import com.zrsoft.mp3rec.utils.RecorderAndPlayUtil;

public class RecordActivity extends Activity{

    private RecorderAndPlayUtil mRecorder = null;
    private boolean mIsRecording = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Button btnStart = (Button) findViewById(R.id.start);
        Button btnStop = (Button) findViewById(R.id.stop);

        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mRecorder.startRecording();
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mRecorder.stopRecording();
            }
        });


        mRecorder = new RecorderAndPlayUtil();

        mRecorder.getRecorder().setHandle(new Handler() {

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
        });
    }



    private void initRecording() {
        mRecorder.stopRecording();
        mIsRecording = false;
    }
}
