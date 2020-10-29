package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/*
*
* 사용자가 입력한 식물의 사진과 종류를 받아서 저장하는 코드 작성
*
* */
public class PlantInfoActivity extends AppCompatActivity {

    ImageButton btn_info_close;
    TextView plantinfo_kind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activtiy_plantinfo);

        btn_info_close = findViewById(R.id.btn_info_close);
        btn_info_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String plant_kind = intent.getStringExtra("plant_kind");
        if(plant_kind==null){
            finish();
        }
        plantinfo_kind = findViewById(R.id.plantinfo_kind);
        plantinfo_kind.setText(plant_kind);
    }
}
