package com.zrsoft.mp3rec.ripple;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.zrsoft.mp3rec.R;

import java.util.ArrayList;

/**
 * 
 * @ClassName:RippleTestActivity
 * @author: shimy
 * @date: 2017/8/10 0010 下午 1:27
 * @description: 波纹效果
 */
public class RippleTestActivity extends AppCompatActivity {
    private ImageView foundDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple_test);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

        foundDevice=(ImageView)findViewById(R.id.foundDevice);
        ImageView button=(ImageView)findViewById(R.id.centerImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundDevice();
                    }
                },3000);
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void foundDevice(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }
}
