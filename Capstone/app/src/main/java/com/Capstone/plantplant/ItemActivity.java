package com.capstone.plantplant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class ItemActivity extends AppCompatActivity {
    private final String DEFAULT_VALUE_STRING = "";
    Toolbar toolbar_item;

    String plant_kind,soil_kind;
    TextView main_plant_name,main_regi_date,main_soil_kind,main_pot_size,main_txt_humity;
    ImageButton btn_information,btn_water_information,btn_setting;
    CheckBox ckb_waterlevel;

    ImageView main_drop1,main_drop2,main_drop3,main_drop4,main_drop5; //토양습도 물방을

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        toolbar_item = findViewById(R.id.toolbar_item);
        setSupportActionBar(toolbar_item);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));

        Intent intent = getIntent();
        count = intent.getIntExtra("count",0);
        if(count==0){
            finish();
        }

        //메모리에 저장된 데이터 불러오기
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        plant_kind = prefs.getString("plant_kind"+count,DEFAULT_VALUE_STRING);
        main_plant_name = findViewById(R.id.main_plant_name);
        main_plant_name.setText(plant_kind);
        main_regi_date = findViewById(R.id.main_regi_date);
        main_regi_date.setText(prefs.getString("reg_date"+count,DEFAULT_VALUE_STRING));
        main_soil_kind = findViewById(R.id.main_soil_kind);
        soil_kind = prefs.getString("soil_kind"+count,DEFAULT_VALUE_STRING);
        main_soil_kind.setText(soil_kind);
        main_pot_size = findViewById(R.id.main_pot_size);
        main_pot_size.setText(prefs.getString("pot_size"+count,DEFAULT_VALUE_STRING));

        btn_information = findViewById(R.id.btn_information);
        btn_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),PlantInfoActivity.class);
                info.putExtra("plant_kind",plant_kind);
                info.putExtra("soil_kind",soil_kind);
                startActivity(info);
            }
        });

        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set = new Intent(getApplicationContext(),ControlActivity.class);
                set.putExtra("count",count);
                startActivity(set);
            }
        });

        initStateBlock(48,true);
    }
    //상태정보 블록을 초기화하는 메소드
    private void initStateBlock(int humity,boolean isEnough){
        btn_water_information = findViewById(R.id.btn_water_information);
        btn_water_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),WaterActivity.class);
                startActivity(info);
            }
        });
        main_txt_humity = findViewById(R.id.main_txt_humity);
        main_drop1 = findViewById(R.id.main_drop1);
        main_drop2 = findViewById(R.id.main_drop2);
        main_drop3 = findViewById(R.id.main_drop3);
        main_drop4 = findViewById(R.id.main_drop4);
        main_drop5 = findViewById(R.id.main_drop5);

        //토양 습도 센서로부터 받은 값 초기화
        main_txt_humity.setText(Integer.toString(humity)+"%");
        viewDropImage(humity);

        //수위 센서로부터 받은 값 초기화
        ckb_waterlevel = findViewById(R.id.ckb_waterlevel);
        ckb_waterlevel.setChecked(isEnough);
        if(isEnough){
            ckb_waterlevel.setText("물이 충분해요!");
        }else {
            ckb_waterlevel.setText("물이 부족해요ㅠ");
        }

    }

    //토양습도를 물방울 이미지로 표현하는 메소드
    private void viewDropImage(int percent){
        int invaild = ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary3);
        main_drop1.setColorFilter(invaild);
        main_drop2.setColorFilter(invaild);
        main_drop3.setColorFilter(invaild);
        main_drop4.setColorFilter(invaild);
        main_drop5.setColorFilter(invaild);

        int vaild = ContextCompat.getColor(getApplicationContext(),R.color.colorAccent);
       if(percent>=20){
           main_drop5.setColorFilter(vaild);
       }
       if(percent>=40){
           main_drop4.setColorFilter(vaild);
       }
       if(percent>=60){
           main_drop3.setColorFilter(vaild);
       }
       if(percent>=80){
           main_drop2.setColorFilter(vaild);
       }
       if(percent>=100){
           main_drop1.setColorFilter(vaild);

       }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}