package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.TipAdapter;
import com.capstone.plantplant.model.ItemTip;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

/*
*
* 사용자가 입력한 식물의 종류를 받아서 저장하는 코드 작성
*
* */
public class PlantInfoActivity extends AppCompatActivity {
    private final String DEFAULT_VALUE_STRING = "";

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
    //토양 정보를 저장해두는 DB 생성
}
