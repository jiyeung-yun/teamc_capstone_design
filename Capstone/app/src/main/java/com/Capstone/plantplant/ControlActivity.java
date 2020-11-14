package com.capstone.plantplant;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import static com.capstone.plantplant.ListActivity.LIST_URI;


public class ControlActivity extends AppCompatActivity {
    Toolbar toolbar_control;

    AlertDialog.Builder dialog;

    Button btn_control_save,btn_reset_data;
    Spinner cont_spinner_time;
    Button btn_save;

    EditText editText_watertime,editText_waterdate,editText_waterhumidity;
    String watertime="",waterdate="",waterhumidity="";

    Spinner spinner_control_soil,spinner_control_pot;
    int index;

    Uri uri = new Uri.Builder().build().parse(LIST_URI);
    String[] colums = {"soil","size"};

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

        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);


        Cursor cursor = getContentResolver().query(uri,colums,"_index="+index,null,null);
        while(cursor.moveToNext()) {
            //토양 종류
            spinner_control_soil = findViewById(R.id.spinner_control_soil);
            int soil_kind_pos = cursor.getInt(cursor.getColumnIndex(colums[0]));
            spinner_control_soil.setSelection(soil_kind_pos);

            //화분 사이즈
            spinner_control_pot = findViewById(R.id.spinner_control_pot);
            int pot_size_pos = cursor.getInt(cursor.getColumnIndex(colums[1]));
            spinner_control_pot.setSelection(pot_size_pos);
        }

        //입력한 정보 저장
        btn_control_save = findViewById(R.id.btn_control_save);
        btn_control_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력한 정보를 기기 내 DB에 업데이트 후 모듈에 전달
                ContentValues values = new ContentValues();
                values.put("soil", spinner_control_soil.getSelectedItemPosition());
                values.put("size", spinner_control_pot.getSelectedItemPosition());
                int count = getContentResolver().update(uri,values,"_index="+index,null);
                Log.d("데이터베이스;식물리스트",  "UPDATE 결과 =>"+count+"개의 컬럼이 변경되었습니다.");

                Toast.makeText(getApplicationContext(),"성공적으로 설정하였습니다.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),ItemActivity.class);
                i.putExtra("index",index);
                startActivity(i);
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
    private boolean clearDeviceData(int index){
        //모듈과의 연결을 끊어야함

        int count = getContentResolver().delete(uri,"_index="+index,null);
        Log.d("데이터베이스;식물리스트",  "DELETE 결과 =>"+count+"개의 컬럼이 삭제되었습니다.");

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(getApplicationContext(),ItemActivity.class);
            i.putExtra("index",index);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
