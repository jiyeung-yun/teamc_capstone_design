package com.capstone.plantplant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class ItemActivity extends AppCompatActivity {
    private final String DEFAULT_VALUE_STRING = "";

    String plant_kind,soil_kind;
    TextView main_plant_name,main_regi_date,main_soil_kind,main_pot_size;
    CardView view_potplant,view_plantstate;
    ImageButton btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set = new Intent(getApplicationContext(),ControlActivity.class);
                startActivity(set);
            }
        });

        Intent intent = getIntent();
        int count = intent.getIntExtra("count",0);
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

        view_potplant = findViewById(R.id.view_potplant);
        view_potplant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),PlantInfoActivity.class);
                info.putExtra("plant_kind",plant_kind);
                info.putExtra("soil_kind",soil_kind);
                startActivity(info);
            }
        });

        view_plantstate = findViewById(R.id.view_plantstate);
        view_plantstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),WaterActivity.class);
                startActivity(info);
            }
        });
    }
}