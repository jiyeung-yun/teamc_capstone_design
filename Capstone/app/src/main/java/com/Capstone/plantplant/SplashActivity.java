package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    //스플래쉬 화면 유지 시간
    final long delay = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("LAUNCH_ALREADY", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("is_launch_before",false)){
            SharedPreferences.Editor editor= prefs.edit();
            editor.putBoolean("is_launch_before",true);
            editor.apply();

            //토양 db 데이터 불러오기
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                finish();
            }
        },delay);
    }
}
