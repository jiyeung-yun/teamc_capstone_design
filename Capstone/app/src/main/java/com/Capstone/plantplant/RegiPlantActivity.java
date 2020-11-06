package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class RegiPlantActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 1011;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SharedPreferences prefs;

    Toolbar toolbar_regiplant;

    EditText eb_kindplant;
    ImageView reg_plant_image;
    Spinner spinner_pot,spinner_soil;
    ToggleButton btn_connect;
    Button btn_regi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiplant);

        /*상단 툴바 기본 설정 초기화*/
        toolbar_regiplant = findViewById(R.id.toolbar_regiplant);
        setSupportActionBar(toolbar_regiplant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*상단 툴바 기본 설정 초기화*/

        prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        eb_kindplant = findViewById(R.id.eb_kindplant);

        reg_plant_image = findViewById(R.id.reg_plant_image);
        reg_plant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent img = new Intent(getApplicationContext(),StorageActivity.class);
                startActivityForResult(img,REQUEST_IMAGE);
            }
        });

        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Toast.makeText(getApplicationContext(),"모듈과 연결합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"모듈과 연결을 끊습니다.",Toast.LENGTH_SHORT).show();
                }
                checkConnectState(b);
            }
        });

        btn_regi = findViewById(R.id.btn_regi);
        btn_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor= prefs.edit();

                String plant_kind = eb_kindplant.getText().toString();
                if(plant_kind.length()<1){
                    Toast.makeText(getApplicationContext(),"식물 종류를 입력해주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //기기에 등록된 식물의 갯수에 +1
                int count =  prefs.getInt("register", 0) + 1;
                editor.putInt("register",count);

                editor.putString("plant_kind"+count,plant_kind);

                //화분의 사이즈
                spinner_pot = findViewById(R.id.spinner_pot);
                String pot_size = spinner_pot.getSelectedItem().toString();
                editor.putString("pot_size"+count,pot_size);

                //토양의 종류
                spinner_soil = findViewById(R.id.spinner_soil);
                String soil_kind = spinner_soil.getSelectedItem().toString();
                editor.putString("soil_kind"+count,soil_kind);

                //등록버튼 클릭 당시 날짜를 받아서 저장함
                String reg_date = dateFormat.format(new Date());
                editor.putString("reg_date"+count,reg_date);


                editor.apply();
                setResult(RESULT_OK);
                finish();
            }
        });
        checkConnectState(btn_connect.isChecked());
    }
    //모듈과의 연결이 확인되면 등록버튼 활성화
    private void checkConnectState(boolean check){
        if(check){
            btn_regi.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_vaild));
        }else{
            btn_regi.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_invaild));
        }
        btn_regi.setClickable(check);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE){
            if(resultCode==RESULT_OK){
                Uri uri = MediaStore.Images.Media.getContentUri("user_plant_image");
                Bitmap image = BitmapFactory.decodeFile(String.valueOf(uri));
                reg_plant_image.setImageBitmap(image);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
