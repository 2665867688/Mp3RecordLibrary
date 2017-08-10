package com.zrsoft.mp3rec;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zrsoft.mp3rec.utils.RecordHelpUtil;
import com.zrsoft.mp3rec.utils.Utils;

public class CompleteActivity extends AppCompatActivity implements View.OnClickListener{

    private Uri fileUri;
    private String provider = "com.zrsoft.mp3rec.fileprovider";
    private String filePath;
    private String fileName;
    private TextView tvShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        tvShow = (TextView) findViewById(R.id.tv_show_name);
        tvShow.setOnClickListener(this);
        filePath = RecordHelpUtil.getSDPath() + "LameMP3/Voice/";
        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = "yue"+System.currentTimeMillis() + ".mp3";
                fileUri = RecordHelpUtil.getOutputMediaFileUri(CompleteActivity.this,filePath,fileName,true,provider);
                GoRecordActivity.startThisContext(CompleteActivity.this,fileUri,false,101);
            }
        });
        findViewById(R.id.btn_out_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompleteActivity.this, OutCallRecordActivity.class);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,)
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 101:
                    tvShow.setText(RecordHelpUtil.getPath(this,fileUri));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_show_name:
                Utils.startAudio(this,fileUri);
                break;
        }
    }
}
