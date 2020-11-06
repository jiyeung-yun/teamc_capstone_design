package com.capstone.plantplant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class ControlActivity extends AppCompatActivity {
    Toolbar toolbar_control;

    AlertDialog.Builder dialog;

    Button btn_control_save,btn_reset_data;
    Spinner cont_spinner_time;

    EditText editText_watertime,editText_waterdate,editText_waterhumidity;
    String watertime="",waterdate="",waterhumidity="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        toolbar_control = findViewById(R.id.toolbar_control);
        setSupportActionBar(toolbar_control);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));

        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("데이터초기화");
        dialog.setMessage("정말로 삭제하시겠습니까?\n해당 식물 정보는 모두 삭제됩니다.");
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(clearDeviceData(getIntent().getIntExtra("count",0))){
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


        cont_spinner_time = (Spinner)findViewById(R.id.control_spinner);

        ArrayList arrayList = new ArrayList<>();
        arrayList.add("오전");
        arrayList.add("오후");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        cont_spinner_time.setAdapter(adapter);

        btn_control_save = findViewById(R.id.btn_control_save);
        btn_control_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"성공적으로 설정하였습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        editText_watertime = findViewById(R.id.editText_watertime);
        editText_watertime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);
                    if (integer > 12 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "시간 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_watertime.setText(watertime);
                    }else {
                        watertime = Integer.toString(integer);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editText_waterdate = findViewById(R.id.editText_waterdate);
        editText_waterdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);
                    if (integer > 365 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "기간 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_waterdate.setText(waterdate);
                    }else {
                        waterdate = Integer.toString(integer);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editText_waterhumidity = findViewById(R.id.editText_waterhumidity);
        editText_waterhumidity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);
                    if (integer > 100 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "기준 습도 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_waterhumidity.setText(waterhumidity);
                    }else {
                        waterhumidity =  Integer.toString(integer);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    //기기 내 메모리 데이터 초기화하기
    private boolean clearDeviceData(int count){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove("plant_kind"+count);
        edit.remove("reg_date"+count);
        edit.remove("soil_kind"+count);
        edit.remove("pot_size"+count);
        edit.commit();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
