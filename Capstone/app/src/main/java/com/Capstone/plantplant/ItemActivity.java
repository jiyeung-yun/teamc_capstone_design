package com.capstone.plantplant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.capstone.plantplant.db.DatabaseHelpter.ALL_COLUMS;
import static com.capstone.plantplant.ListActivity.LIST_URI;

public class ItemActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CONTROL = 5000;
    Toolbar toolbar_item;

    String plant_kind,soil_kind;
    TextView main_plant_name,main_regi_date,main_soil_kind,main_pot_size,main_txt_humity;
    ImageView main_plant_image;

    Button btn_information,btn_water_information,btn_setting;
    CheckBox ckb_waterlevel;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        /*액션바 => 툴바로 적용 -시작-*/
        toolbar_item = findViewById(R.id.toolbar_item);
        setSupportActionBar(toolbar_item);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*액션바 => 툴바로 적용 -끝-*/

        //이전 액티비티에서 전달한 아이템 count 정보를 입력받음
        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        Cursor cursor = getContentResolver().query(uri,ALL_COLUMS,"_index="+index,null,null);
        Log.d("ItemActivity","SQL result : "+cursor.getColumnCount());
        while(cursor.moveToNext()){
            //식물의 종류

            plant_kind = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[1]));
            Log.d("ItemActivity",cursor.getColumnName(1)+" : "+plant_kind);

            main_plant_name = findViewById(R.id.main_plant_name);
            main_plant_name.setText(plant_kind);

            //아이템 등록 날짜
            main_regi_date = findViewById(R.id.main_regi_date);
            String date = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[2]));
            main_regi_date.setText(date);
            Log.d("ItemActivity",cursor.getColumnName(2)+" : "+date);

            //토양 종류
            main_soil_kind = findViewById(R.id.main_soil_kind);
            int soil_kind_pos = cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[3]));

            Log.d("ItemActivity",cursor.getColumnName(3)+" : "+soil_kind_pos);


            String[] arr = getResources().getStringArray(R.array.soil_array);
            soil_kind = arr[soil_kind_pos];
            main_soil_kind.setText(soil_kind);


            //화분 사이즈
            main_pot_size = findViewById(R.id.main_pot_size);
            int pot_size_pos = cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[4]));
            Log.d("ItemActivity",cursor.getColumnName(4)+" : "+pot_size_pos);


            String[] arr2 = getResources().getStringArray(R.array.pot_array);
            String pot_size = arr2[pot_size_pos];
            main_pot_size.setText(pot_size);

            String filename = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[6]));
            Log.d("ItemActivity",cursor.getColumnName(6)+" : "+filename);

            String path = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[7]));
            Log.d("ItemActivity",cursor.getColumnName(7)+" : "+path);

            //사진 보여주는 코드
            if(filename!=null && path!=null){
                try {
                    File file=new File(path, filename);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                    main_plant_image = findViewById(R.id.main_plant_image);
                    main_plant_image.setImageBitmap(bitmap);
                    main_plant_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        //식물의 종류에 따른 식물정보 액티비티 버튼
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

        //제어 액티비티 버튼
        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set = new Intent(getApplicationContext(),ControlActivity.class);
                set.putExtra("index",index);
                startActivityForResult(set,REQUEST_CODE_CONTROL);
            }
        });
        //급수 정보 액티비티 버튼
        btn_water_information = findViewById(R.id.btn_water_information);
        btn_water_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),WaterActivity.class);
                info.putExtra("index",index);
                startActivity(info);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        //상태정보 블록을 초기화하는 메소드
        initStateBlock(48,true);
    }

    //토양습도 물방을
    ImageView main_drop1,main_drop2,main_drop3,main_drop4,main_drop5;

    //상태정보 블록을 초기화하는 메소드
    private void initStateBlock(int humity,boolean isEnough){
        //토양 습도 센서로부터 받은 값 초기화
        viewDropImage(humity);

        //수위 센서로부터 받은 값 초기화
        ckb_waterlevel = findViewById(R.id.ckb_waterlevel);
        ckb_waterlevel.setClickable(false);
        ckb_waterlevel.setChecked(isEnough);
        if(isEnough){
            ckb_waterlevel.setText("물이 충분해요!");
        }else {
            ckb_waterlevel.setText("물이 부족해요ㅠ");
        }
    }

    //토양습도를 물방울 이미지로 표현하는 메소드
    private void viewDropImage(int percent){
        main_txt_humity = findViewById(R.id.main_txt_humity);
        main_txt_humity.setText(percent+"%");

        int invaild = ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary3);

        main_drop1 = findViewById(R.id.main_drop1);
        main_drop1.setColorFilter(invaild);

        main_drop2 = findViewById(R.id.main_drop2);
        main_drop2.setColorFilter(invaild);

        main_drop3 = findViewById(R.id.main_drop3);
        main_drop3.setColorFilter(invaild);

        main_drop4 = findViewById(R.id.main_drop4);
        main_drop4.setColorFilter(invaild);

        main_drop5 = findViewById(R.id.main_drop5);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_CONTROL){
            if(resultCode==RESULT_OK){
                finish();
            }
        }

    }
}