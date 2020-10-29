package com.capstone.plantplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegiPlantActivity extends AppCompatActivity {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    EditText eb_kindplant;
    Spinner spinner_pot,spinner_soil;
    ToggleButton btn_connect;
    Button btn_regi;

    Date regi_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiplant);

        eb_kindplant = findViewById(R.id.eb_kindplant);
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
                Intent reg = new Intent(getApplicationContext(),MainActivity.class);
                String plant_kind = eb_kindplant.getText().toString();
                if(plant_kind.length()<1){
                    Toast.makeText(getApplicationContext(),"식물 종류를 입력해주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                reg.putExtra("plant_kind",plant_kind);

                //화분의 사이즈
                spinner_pot = findViewById(R.id.spinner_pot);
                reg.putExtra("pot_size",spinner_pot.getSelectedItem().toString());

                //토양의 종류
                spinner_soil = findViewById(R.id.spinner_soil);
                reg.putExtra("soil_kind",spinner_soil.getSelectedItem().toString());

                //등록버튼 클릭 당시 날짜를 받아서 저장함
                regi_date = new Date();
                reg.putExtra("reg_date",dateFormat.format(regi_date));

                spinner_pot.getSelectedItem().toString();

                startActivity(reg);
                finish();
            }
        });
        checkConnectState(btn_connect.isChecked());
    }
    private void checkConnectState(boolean check){
        if(check){
            btn_regi.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_vaild));
        }else{
            btn_regi.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_invaild));
        }
        btn_regi.setClickable(check);
    }
}
