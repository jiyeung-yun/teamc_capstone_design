package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.util.PreferenceManager;
import retrofit2.http.Body;


public class SplashActivity extends AppCompatActivity {

    //스플래쉬 화면 유지 시간
    final long delay = 1500;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        PreferenceManager preferenceManager = new PreferenceManager();
        if (!preferenceManager.getBoolean(context, "FirstDrive")) {
            Intent intent = new Intent(getApplicationContext(),DriveActivity.class);
            startActivity(intent);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(intent);
                    finish();

                }
            }, delay);
        }


    }

}
