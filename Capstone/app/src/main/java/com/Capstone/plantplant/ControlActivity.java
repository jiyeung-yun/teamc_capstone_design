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
    Button btn_save;

    EditText editText_watertime,editText_waterdate,editText_waterhumidity;
    String watertime="",waterdate="",waterhumidity="";

    Spinner spinner_control_soil,spinner_control_pot;
    int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        /*액션바 => 툴바로 적용*/
        toolbar_control = findViewById(R.id.toolbar_control);
        setSupportActionBar(toolbar_control);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*액션바 => 툴바로 적용*/

        //데이터를 삭제할 경우 보여지는 알림창 초기화
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
        //데이터를 삭제할 경우 보여지는 알림창 생성
        dialog.create();

        //데이터 삭제 버튼
        btn_reset_data = findViewById(R.id.btn_reset_data);
        btn_reset_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        //오전/오후 스피너 초기화
        cont_spinner_time = (Spinner)findViewById(R.id.control_spinner);
        ArrayList arrayList = new ArrayList<>();
        arrayList.add("오전");
        arrayList.add("오후");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        cont_spinner_time.setAdapter(adapter);
        //오전/오후 스피너 초기화

        //이전 액티비티에서 전달한 아이템 count 정보를 입력받음
        Intent intent = getIntent();
        count = intent.getIntExtra("count",0);
        if(count==0){
            finish();
        }

        /*count 값으로 기기 내 메모리 데이터 확인*/
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        spinner_control_soil = findViewById(R.id.spinner_control_soil);
        int soil_kind_pos = prefs.getInt("soil_kind_pos"+count,0);
        spinner_control_soil.setSelection(soil_kind_pos);

        spinner_control_pot = findViewById(R.id.spinner_control_pot);
        int pot_size_pos = prefs.getInt("pot_size_pos"+count,0);
        spinner_control_pot.setSelection(pot_size_pos);
        /*count 값으로 기기 내 메모리 데이터 확인*/

        //입력한 정보 저장
        btn_control_save = findViewById(R.id.btn_control_save);
        btn_control_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력한 정보를 기기 내 count 데이터에 업데이트 후 모듈에 전달
                Toast.makeText(getApplicationContext(),"성공적으로 설정하였습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        /*물 주는 시간 입력 edittext 초기화 -시작-*/
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
                    //입력값이 0~12가 되도록
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
        /*물 주는 시간 입력 edittext 초기화 -끝-*/

        /*물 주는 기간 입력 edittext 초기화 -시작-*/
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

                    //입력값이 0~365범위가 되도록
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
        /*물 주는 기간 입력 edittext 초기화 -끝-*/

<<<<<<< HEAD
        // 설정 저장
        btn_save = (Button)findViewById(R.id.control_btn_save);
    }


    //기기 내 메모리 데이터 초기화하기
    private boolean clearDeviceData(){
=======
        /*기준 습도 입력 edittext 초기화 -시작-*/
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
                    //입력값이 0~100이 되도록
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
        /*기준 습도 입력 edittext 초기화 -끝-*/

    }
    //식물 아이템 삭제 메소드
    private boolean clearDeviceData(int count){

        //모듈과의 연결을 끊어야함

        //기기 내 메모리 데이터 초기화하기
>>>>>>> a9fce70ef992d027207d54069415146bd009b11e
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove("plant_kind"+count);
        edit.remove("reg_date"+count);
        edit.remove("soil_kind_pos"+count);
        edit.remove("pot_size_pos"+count);
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
