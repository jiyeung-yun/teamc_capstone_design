package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    public static final String PREFERENCES_NAME = "DeviceMem";
    private final boolean DEFAULT_VALUE_BOOLEAN = false;

    SharedPreferences prefs;
    Intent mintent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * 등록된 식물 정보가 있는지 확인 후 넘겨줘야 할 화면 결정
        * 식물 정보 있다 > 메인화면
        * 식물 정보 없다 > 등록화면
        * */
        prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prefs.getBoolean("register", DEFAULT_VALUE_BOOLEAN)){
                    //등록된 식물이 있을 경우
                    mintent = new Intent(getApplicationContext(), MainActivity.class);
                }else {
                    //등록된 식물이 없을 경우
                    mintent = new Intent(getApplicationContext(), RegiPlantActivity.class);
                }
                startActivity(mintent);
                finish();
            }
        },1500);
    }
}
