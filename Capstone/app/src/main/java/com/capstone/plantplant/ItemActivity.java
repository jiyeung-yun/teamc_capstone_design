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
import android.widget.ImageButton;
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
    TextView main_plant_name,main_regi_date,main_soil_kind;
    ImageView main_plant_image;

    ImageButton btn_setting;
    Button btn_information;

    int index;

    TextView txt_prev_date,txt_prev_vol;

    @Override
    protected void onStart() {
        super.onStart();

        //상태정보 블록을 초기화하는 메소드
        initStateBlock(48,26,true);
    }

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
                Intent set = new Intent(getApplicationContext(), SettingActivity.class);
                set.putExtra("index",index);
                startActivityForResult(set,REQUEST_CODE_CONTROL);
            }
        });


    }

    TextView main_txt_humity,main_txt_temp,main_txt_water_vol;
    ImageView main_img_water_vol;

    //상태정보 블록을 초기화하는 메소드
    private void initStateBlock(int humity,int temp,boolean isEnough){
        //토양 습도 센서로부터 받은 값 초기화
        main_txt_humity = findViewById(R.id.main_txt_humity);
        main_txt_humity.setText(humity+"%");

        //온도 센서로부터 받은 값 초기화
        main_txt_temp = findViewById(R.id.main_txt_temp);
        main_txt_temp.setText(temp+"ºC");

        //수위 센서로부터 받은 값 초기화
        main_img_water_vol = findViewById(R.id.main_img_water_vol);
        main_txt_water_vol = findViewById(R.id.main_txt_water_vol);
        if(isEnough){
            main_img_water_vol.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_sentiment_satisfied_24px));
            main_txt_water_vol.setText("충분");
        }else {
            main_img_water_vol.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_sentiment_dissatisfied_24px));
            main_txt_water_vol.setText("부족");
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