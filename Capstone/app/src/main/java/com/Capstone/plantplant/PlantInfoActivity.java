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
* 사용자가 입력한 식물의 종류를 받아서 저장하는 코드 작성
*
*/
public class PlantInfoActivity extends AppCompatActivity {
    ImageButton btn_info_close;
    TextView txt_plantinfo_kind,txt_plantinfo_soil;

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
        String  plant_kind = intent.getStringExtra("plant_kind");
        txt_plantinfo_kind = findViewById(R.id.txt_plantinfo_kind);
        txt_plantinfo_kind.setText(plant_kind);

        String  soil_kind = intent.getStringExtra("soil_kind");
        txt_plantinfo_soil = findViewById(R.id.txt_plantinfo_soil);
        txt_plantinfo_soil.setText(soil_kind);

    }
    //토양 정보를 저장해두는 DB 생성한 후 로드
    private void LoadSoilInformation(String soild_name){

    }
    //식물 종류에 따른 종류를 api에서 응답받아 로드
    private void LoadPlantInformation(String plant_name){

    }
}
