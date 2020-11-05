package com.capstone.plantplant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class ControlActivity extends AppCompatActivity {
    AlertDialog.Builder dialog;

    Button btn_reset_data;
    Spinner cont_spinner_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);


        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("데이터초기화");
        dialog.setMessage("정말로 초기화하시겠습니까? 모든 정보는 삭제됩니다.");
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(clearDeviceData()){
                    Intent reset = new Intent(getApplicationContext(),RegiPlantActivity.class);
                    startActivity(reset);
                    finish();
                }
            }
        });
        dialog.create();

        btn_reset_data = findViewById(R.id.btn_reset_data);
        btn_reset_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        // 기기 전원

        // 물 공급량

        // 물 주는 시간을 입력받는 기능
        cont_spinner_time = (Spinner)findViewById(R.id.control_spinner);

        ArrayList arrayList = new ArrayList<>();
        arrayList.add("오전");
        arrayList.add("오후");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        cont_spinner_time.setAdapter(adapter);

        // 물 주는 기간 입력받는 기능 => 입력박스


    }
    //기기 내 메모리 데이터 초기화하기
    private boolean clearDeviceData(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
        return true;
    }

}
