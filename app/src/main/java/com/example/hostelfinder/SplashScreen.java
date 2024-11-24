package com.example.hostelfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);




       //lottieAnimationView.animate().translationX(2000).setDuration(2000).setStartDelay(2900);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iHome=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(iHome);
                finish();
            }
        },3000);



    }



}


