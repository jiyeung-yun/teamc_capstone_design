package com.capstone.plantplant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.plantplant.network.API;
import com.capstone.plantplant.network.RetrofitService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.capstone.plantplant.db.ListDBHelper.ALL_COLUMS;
import static com.capstone.plantplant.ListActivity.LIST_URI;

public class ItemActivity extends AppCompatActivity {
    final int REQUEST_CODE_SETTING = 5005;
    Toolbar toolbar_item;

    String plantKind,soilKind;
    TextView main_plant_name,main_regi_date,main_soil_kind;
    ImageView main_plant_image;

    CardView ly_item_img,ly_state_view;

    Button btn_item_info;
    int index;

    TextView txt_prev_date,txt_diff_date;
    ImageButton item_refresh_data;

    AlertDialog.Builder dialog;

    public boolean isConnected(Context context){
        ConnectivityManager cm = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        if(!isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요!", Toast.LENGTH_SHORT).show();
            finish();
        }

        /*액션바 => 툴바로 적용 -시작-*/
        toolbar_item = findViewById(R.id.toolbar_item);
        setSupportActionBar(toolbar_item);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*액션바 => 툴바로 적용 -끝-*/

        main_plant_name = findViewById(R.id.main_plant_name);
        main_regi_date = findViewById(R.id.main_regi_date);

        main_soil_kind = findViewById(R.id.main_soil_kind);
        ly_item_img = findViewById(R.id.ly_item_img);
        ly_state_view = findViewById(R.id.ly_state_view);


        //이전 액티비티에서 전달한 아이템 count 정보를 입력받음
        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        getPlantInfo();

        main_plant_image = findViewById(R.id.main_plant_image);
        /*main_plant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CamActivity.class);
                startActivity(intent);
            }
        });
        */
        btn_item_info = findViewById(R.id.btn_item_info);
        btn_item_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(),PlantInfoActivity.class);
                info.putExtra("index",index);
                info.putExtra("soil_kind",soilKind);
                startActivity(info);
            }
        });

        item_refresh_data = findViewById(R.id.item_refresh_data);
        item_refresh_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getPlantDataToServer(plantKind);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("[알림] 식물에 물을 줘야할 것 같아요!");
        dialog.setMessage("자동급수가 설정되어있는지 확인해보세요!\n설정창에서 직접 물을 줄 수도 있습니다.\n\"확인\"을 누르시면 설정창으로 이동합니다.");
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               goToSetting();
            }
        });

        //데이터를 삭제할 경우 보여지는 알림창 생성
        dialog.create();

    }

    private void getPlantDataToServer(String plant_name) throws UnsupportedEncodingException {
        if(!isConnected(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }
        Snackbar.make(ly_state_view, "연결된 화분의 정보를 불러오는 중입니다.", Snackbar.LENGTH_SHORT).show();
        API api_ = RetrofitService.getServer().create(API.class);
        Call<ResponseBody> call = api_.getPlantData(plant_name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    if(response.body()!=null){
                        try {
                            String body = response.body().string();
                            JSONObject body_ = new JSONObject(body);
                            String item_body = body_.getString("body");
                            if(body_.getInt("statusCode")!=200){
                                Toast.makeText(getApplicationContext(),"연결된 화분정보 불러오던 중에\n오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject item_body_ = new JSONObject(item_body);
                            String item_body2 = item_body_.getString("Item");
                            JSONObject item_data = new JSONObject(item_body2);

                            //온도 센서 값
                            int sensorTemp = item_data.getInt("temperature_sensor");

                            //습도 센서 값
                            int sensorHumidity = item_data.getInt("humidity_sensor");

                            //물탱크 수위센서 값
                            int sensorWater = item_data.getInt("waterlevel_sensor");

                            //기준 습도 값
                            int standard_humidity = item_data.getInt("standard_humidity");
                            if(standard_humidity>sensorHumidity){
                                dialog.show();
                            }

                            String water_date = item_data.getString("water_date");
                            if(water_date==null){
                                water_date = "최근에 급수한 이력이 없습니다.";
                            }

                            initStateBlock(sensorHumidity,sensorTemp,sensorWater,water_date);
                            Snackbar.make(ly_state_view, "연결된 화분정보 불러오기에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"연결된 화분정보 불러오던 중에\n오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    if(response.errorBody()!=null){
                        try {
                            String errormessage = response.errorBody().string();
                            Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    TextView main_txt_humity,main_txt_temp,main_txt_water_vol;
    ImageView main_img_water_vol;


    //상태정보 블록을 초기화하는 메소드
    private void initStateBlock(int humity,int temp,int isEnough, String lastDate) {
        //토양 습도 센서로부터 받은 값 초기화
        main_txt_humity = findViewById(R.id.main_txt_humity);

        if(humity > 100){
            main_txt_humity.setText("ERROR");
        }else {
            main_txt_humity.setText(humity+"%");
        }

        //온도 센서로부터 받은 값 초기화
        main_txt_temp = findViewById(R.id.main_txt_temp);
        if(temp > 10000){
            main_txt_temp.setText("ERROR");
        }else {
            main_txt_temp.setText(temp+"℃");
        }


        //수위 센서로부터 받은 값 초기화
        main_img_water_vol = findViewById(R.id.main_img_water_vol);
        main_txt_water_vol = findViewById(R.id.main_txt_water_vol);
        if(isEnough==1){
            main_img_water_vol.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_sentiment_satisfied_24px));
            main_txt_water_vol.setText("충분");
        }else {
            main_img_water_vol.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_sentiment_dissatisfied_24px));
            main_txt_water_vol.setText("부족");
        }

        txt_prev_date = findViewById(R.id.txt_prev_date);


        txt_diff_date = findViewById(R.id.txt_diff_date);
        Date today = new Date();
        try {
            txt_diff_date.setVisibility(View.VISIBLE);
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(lastDate);
            long calDate = today.getTime() - day.getTime();
            long calDateDays = calDate / ( 24*60*60*1000);
            calDateDays = Math.abs(calDateDays);
            if(calDateDays==0){
                txt_diff_date.setTextColor(Color.BLACK);
                txt_diff_date.setText("오늘");
            }else {
                txt_diff_date.setTextColor(Color.GRAY);
                txt_diff_date.setText((int)calDateDays+"일 전");
            }

            String LD = new SimpleDateFormat("yyyy-MM-dd").format(day);
            txt_prev_date.setText(LD);
        }catch (ParseException e){
            txt_prev_date.setText(lastDate);
            txt_diff_date.setVisibility(View.INVISIBLE);
        }
    }

    private void getPlantInfo(){
        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        Cursor cursor = getContentResolver().query(uri,ALL_COLUMS,"_index="+index,null,null);
        //Log.d("ItemActivity","SQL result : "+cursor.getColumnCount());

        while(cursor.moveToNext()){
            //식물의 종류
            plantKind = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[1]));
            //Log.d("ItemActivity",cursor.getColumnName(1)+" : "+plantKind);
            try {
                //서버에 저장된 데이터를 받기 위해
                getPlantDataToServer(plantKind);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
            main_plant_name.setText(plantKind);

            //아이템 등록 날짜
            String date = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[2]));
            main_regi_date.setText(date);
            //Log.d("ItemActivity",cursor.getColumnName(2)+" : "+date);


            //토양 종류
            int soil_kind_pos = cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[3]));
            //Log.d("ItemActivity",cursor.getColumnName(3)+" : "+soil_kind_pos);

            String[] arr = getResources().getStringArray(R.array.soil_array);
            soilKind = arr[soil_kind_pos];
            main_soil_kind.setText(soilKind);

            String filename = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[6]));
            //Log.d("ItemActivity",cursor.getColumnName(6)+" : "+filename);

            String path = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[7]));
            //Log.d("ItemActivity",cursor.getColumnName(7)+" : "+path);

            //등록된 사진 불러오기
            if(filename!=null && path!=null){
                try {
                    File file=new File(path, filename);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    if(bitmap==null){
                        throw new Exception();
                    }
                    main_plant_image.setImageBitmap(bitmap);
                    main_plant_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    ly_item_img.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                    ly_item_img.setVisibility(View.GONE);
                }
            }else {
                ly_item_img.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }
    private void goToSetting(){
        Intent set = new Intent(getApplicationContext(), SettingActivity.class);
        set.putExtra("index",index);
        startActivityForResult(set,REQUEST_CODE_SETTING);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if(item.getItemId() == R.id.btn_setting){
            goToSetting();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SETTING){
            if(resultCode == RESULT_OK){
                finish();
            }else{
                getPlantInfo();
            }
        }
    }
}