package com.zrsoft.mp3rec;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.zrsoft.mp3rec.lame.MP3Recorder;
import com.zrsoft.mp3rec.utils.RecorderAndPlayUtil;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName:录音server
 * @author: shimy
 * @date: 2017/8/9 0009 上午 8:41
 * @description:
 */
public class RecordService extends Service {

    private RecordBinder binder = new RecordBinder();
    private final static int MSG_TIME = 102;
    private long time;
    private Uri uri;
    private RecorderAndPlayUtil mRecorder = null;
    private boolean isNotification = false;//是否开启前台通知


    public RecordService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isNotification = intent.getBooleanExtra(GoRecordActivity.RECORD_ISNOTIFICATION, false);
        uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class RecordBinder extends Binder {
        public void startRecord() {
            mRecorder = null;
            mRecorder = new RecorderAndPlayUtil(RecordService.this, uri);
            mRecorder.getRecorder().setHandle(handler);
            mRecorder.startRecording();
            timer = new Timer();
            timer.schedule(new RecTimerTask(), 0, 1000);
            if (isNotification) {
                startForeground(1, getNotification("录音", 0));
            }
        }

        public void pauseRecord() {
            mRecorder.pauseRecording();
            timer.cancel();
        }

        public void restoreRecord() {
            mRecorder.restoreRecording();
            timer = new Timer();
            timer.schedule(new RecTimerTask(), 0, 1000);
        }

        public void saveRecord() {
            //保存录音 取消前台通知
            timer.cancel();
            time = 0;
            onRecordListener.recordTime("录音");
            mRecorder.stopRecording();//停止并保存录音
            if (isNotification) {
                getNotificationManager().cancel(1);
                stopForeground(true);
            }

        }

        public void giveUp() {
            //舍弃 将录音文件删除掉 取消前台通知
            timer.cancel();
            time = 0;
            File file = new File(mRecorder.getRecorderPath());
            file.delete();
            if (isNotification) {
                getNotificationManager().cancel(1);
                stopForeground(true);
            }
        }

        public RecordService getRecordService() {
            return RecordService.this;
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, long timer) {
        Intent intent = new Intent(this, GoRecordActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle("录音");
        builder.setContentText(timer + "");
        return builder.build();
    }

    Timer timer = new Timer();

    class RecTimerTask extends TimerTask {

        @Override
        public void run() {
            // 需要做的事:发送消息
            handler.sendEmptyMessage(MSG_TIME);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME://时间改变
                    time++;
                    onRecordListener.recordTime(time + "");
                    if (isNotification) {
                        getNotificationManager().notify(1, getNotification("录音", time));
                    }
                    break;
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

    private OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener listener) {
        this.onRecordListener = listener;
    }

    public interface OnRecordListener {
        void recordTime(String time);
    }
}
