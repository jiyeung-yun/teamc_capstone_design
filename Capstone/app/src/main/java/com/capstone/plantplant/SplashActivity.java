package com.capstone.plantplant;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.db.PlantDBAdapter;

import static com.capstone.plantplant.ListActivity.plantList;


public class SplashActivity extends AppCompatActivity {

    //스플래쉬 화면 유지 시간
    final long delay = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(plantList==null){
            //데이터 베이스 내 식물 정보 로드
            initLoadDB();
        }else {
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
    private void initLoadDB() {
        PlantDBAdapter mDbHelper = new PlantDBAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        plantList = mDbHelper.getTableData();

        // DB 닫기
        mDbHelper.close();

        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
        finish();

    }
}
