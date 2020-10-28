package com.capstone.plantplant;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/*
*
* 사용자가 입력한 식물의 사진과 종류를 받아서 저장하는 코드 작성
*
* */
public class PlantInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_plantinfo);
    }
}
