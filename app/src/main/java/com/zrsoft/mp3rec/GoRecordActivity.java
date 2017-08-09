package com.zrsoft.mp3rec;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zrsoft.mp3rec.lame.MP3Recorder;
import com.zrsoft.mp3rec.utils.RecordHelpUtil;
import com.zrsoft.mp3rec.utils.RecorderAndPlayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName:GoRecordActivity
 * @author: shimy
 * @date: 2017/8/8 0008 下午 3:47
 * @description: 录音 启动此activity需要传递两个参数 uri和isnotification(boolean)
 */
public class GoRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnControl;
    private Button btnSave;
    private Button btnGiveUp;
    private TextView tvTimeShow;
    private Uri uri;
    private int state = 0;//0：开始 1：播放中 2：暂停
    //是否开启前台通知
    public static final String RECORD_ISNOTIFICATION = "RECORD_ISNOTIFICATION";
    private boolean isNotification = false;//是否开启前台通知
    private RecordService.RecordBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (RecordService.RecordBinder) service;
            //向service设置回调接口，service发送通知到activity更新界面
            binder.getRecordService().setOnRecordListener(onRecordListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_record);
        uri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        isNotification = getIntent().getBooleanExtra(NOTIFICATION_SERVICE,true);

        btnControl = (Button) findViewById(R.id.btn_control);
        btnControl.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        btnGiveUp = (Button) findViewById(R.id.btn_give_up);
        btnGiveUp.setOnClickListener(this);
        tvTimeShow = (TextView) findViewById(R.id.tv_show_time);
        //启动service
        Intent intentService = new Intent(this,RecordService.class);
        intentService.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        intentService.putExtra(RECORD_ISNOTIFICATION,false);
        startService(intentService);
        bindService(intentService, connection, BIND_AUTO_CREATE); // 绑定服务
        //获取录音权限
        if (ContextCompat.checkSelfPermission(GoRecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GoRecordActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_control) {
            if (state == 0) {
                binder.startRecord();
                btnControl.setText("暂停");
                state = 1;
            } else if (state == 1) {
                binder.pauseRecord();
                btnControl.setText("继续");
                state = 2;
            } else if (state == 2) {
                binder.restoreRecord();
                btnControl.setText("暂停");
                state = 1;
            }
        } else if (i == R.id.btn_save) {
            //保存录音 取消前台通知
            state = 0;
            binder.saveRecord();
            btnControl.setText("重新录");
            Toast.makeText(this, "保存录音", Toast.LENGTH_SHORT).show();
        } else if (i == R.id.btn_give_up) {
            //舍弃 将录音文件删除掉 取消前台通知
            Toast.makeText(this, "舍弃录音", Toast.LENGTH_SHORT).show();
            state = 0;
            binder.giveUp();
            btnControl.setText("开始");
            tvTimeShow.setText("录音");
        }
    }


    RecordService.OnRecordListener onRecordListener = new RecordService.OnRecordListener() {
        @Override
        public void recordTime(long time) {
            tvTimeShow.setText(RecordHelpUtil.misToTime(time*1000));
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * 使用此方法启动此activity
     * @param context
     * @param uri
     * @param isNotification
     */
    public static void startThisContext(Context context,Uri uri, boolean isNotification){
        Intent intent = new Intent(context, GoRecordActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        intent.putExtra(NOTIFICATION_SERVICE,isNotification);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        Intent intentService = new Intent(this,RecordService.class);
        stopService(intentService);
    }
}
