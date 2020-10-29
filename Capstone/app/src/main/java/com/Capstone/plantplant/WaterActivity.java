package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WaterActivity extends AppCompatActivity {

    ImageButton btn_info_close2;

    TextView txt_next_date,txt_next_vol;
    TextView txt_prev_date,txt_prev_vol;

    Button btn_setting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_water);

        btn_info_close2 = findViewById(R.id.btn_info_close2);
        btn_info_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LoadPrevData();
        LoadNextData();

        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set = new Intent(getApplicationContext(),ControlActivity.class);
                startActivity(set);
                finish();
            }
        });

    }
    //최근 모듈이 물 주기를 수행한 날짜와 용량
    private void LoadPrevData(){
        /*
        * DB에서 저장된 기록 불러오기?
        * 서버에 가장 최신의 데이터만 저장하여 저장된 기록 불러오기?
        * */

        txt_prev_date = findViewById(R.id.txt_prev_date);
        txt_prev_date.setText("2020-10-30");
        txt_prev_vol = findViewById(R.id.txt_prev_vol);
        txt_prev_vol.setText("40mL");

    }
    //다음 번에 모듈이 물 주기를 수행한 날짜와 용량
    private void LoadNextData(){
        txt_next_date = findViewById(R.id.txt_next_date);
        txt_next_date.setText("2020-11-05");
        txt_next_vol = findViewById(R.id.txt_next_vol);
        txt_next_vol.setText("65mL");

    }
}