package com.Capstone.plantmana;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * 등록된 식물 정보가 있는지 확인 후 넘겨줘야 할 화면 결정
        * 식물 정보 있다 > 메인화면
        * 식물 정보 없다 > 등록화면
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //등록된 식물이 있을 경우
                Intent mintent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mintent);
            }
        },1500);
    }
}
