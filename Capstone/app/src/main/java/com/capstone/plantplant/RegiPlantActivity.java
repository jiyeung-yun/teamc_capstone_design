package com.capstone.plantplant;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.capstone.plantplant.model.Plantcntnts;
import com.capstone.plantplant.model.PlantToServer;
import com.capstone.plantplant.network.API;
import com.capstone.plantplant.network.RetrofitService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.capstone.plantplant.ListActivity.APIKEY;
import static com.capstone.plantplant.ListActivity.LIST_URI;

public class RegiPlantActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG ="RegiPlantActivity_State";

    private static final int REQUEST_IMAGE = 1011;
    private static final int REQUEST_PLANT_KIND = 1012;
    private static final int REQUEST_SERVER_DEVICE = 1013;

    Toolbar toolbar_regiplant;

    TextView txt_kindplant;

    ImageView reg_plant_image;
    ImageButton btn_regi_img;

    Spinner spinner_soil;

    Button btn_connect,btn_regi;

    Plantcntnts plantcntnts;
    int serial_device = 0; //디바이스 번호
    String plant_img, path; //사진 파일 경로


    final int FIRST_WATER_CODE = 100; //항상 흙을 축축하게 유지함(물에 잠김)
    final int SECOND_WATER_CODE = 70; //흙을 촉촉하게 유지함(물에 잠기지 않도록 주의)
    final int THIRD_WATER_CODE = 40; //토양 표면이 말랐을때 충분히 관수함
    final int FOURTH_WATER_CODE = 10; //화분 흙 대부분 말랐을때 충분히 관수함


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiplant);
        toolbar_regiplant = findViewById(R.id.toolbar_regiplant);
        setSupportActionBar(toolbar_regiplant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));

        //식물의 종류를 검색하는 화면 버튼
        txt_kindplant = findViewById(R.id.txt_kindplant);
        txt_kindplant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kind  = new Intent(getApplicationContext(),SearchPlantActivity.class);
                startActivityForResult(kind,REQUEST_PLANT_KIND);
            }
        });

        //식물의 사진을 입력받는 버튼
        reg_plant_image = findViewById(R.id.reg_plant_image);
        btn_regi_img = findViewById(R.id.btn_regi_img);
        btn_regi_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent img = new Intent(getApplicationContext(),StorageActivity.class);
                startActivityForResult(img,REQUEST_IMAGE);
            }
        });

        //모듈 연결 확인 버튼
        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mBluetoothLeService.disconnect();
                Intent intent1 = new Intent(getApplicationContext(), DeviceActivity.class);
                startActivityForResult(intent1,REQUEST_SERVER_DEVICE);
                // finish();
            }
        });

        //식물 아이템 등록 버튼
        btn_regi = findViewById(R.id.btn_regi);
        btn_regi.setOnClickListener(this);

        onViewPrograss(false);

    }
    ProgressBar regi_progressBar;
    private void onViewPrograss(boolean view){
        regi_progressBar = findViewById(R.id.regi_progressBar);
        regi_progressBar.setIndeterminate(view);

        if(view){
            regi_progressBar.setVisibility(View.VISIBLE);
        }else{
            regi_progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_IMAGE:{

                if(resultCode==RESULT_OK){

                    plant_img = data.getStringExtra("filename");
                    Log.d(TAG,"filename is "+plant_img);

                    path = data.getStringExtra("path");
                    Log.d(TAG,"file path is "+path);

                    try {
                        File file=new File(path, plant_img);
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        Log.d(TAG,"filename load complete");

                        reg_plant_image.setImageBitmap(bitmap);
                        reg_plant_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;
            }
            case REQUEST_PLANT_KIND:{

                if(resultCode==RESULT_OK){
                    plantcntnts = new Plantcntnts();

                    final String num = data.getStringExtra("result_kind_num");
                    plantcntnts.setCntntsNo(num);
                    String sj = data.getStringExtra("result_kind_name");
                    plantcntnts.setCntntsSj(sj);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            getPlantInformation(num);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    int codenum = 0;
                                    codenum = Integer.parseInt(water_temp);

                                    switch (codenum){
                                        case 53001 : {
                                            plantcntnts.setHdCode(FIRST_WATER_CODE); break;}
                                        case 53002  : {
                                            plantcntnts.setHdCode(SECOND_WATER_CODE); break;}
                                        case 53003 : {
                                            plantcntnts.setHdCode(THIRD_WATER_CODE); break;}
                                        case 53004  : {
                                            plantcntnts.setHdCode(FOURTH_WATER_CODE); break;}
                                    }
                                }
                            });

                        }
                    }).start();

                    txt_kindplant.setText(sj);
                }
                break;
            }
            case REQUEST_SERVER_DEVICE:{
                if(resultCode==RESULT_OK){
                    serial_device = data.getIntExtra("result_device_num",0);
                    if(serial_device!=0){
                        btn_connect.setText("연결완료");
                    }
                }
                break;
            }
        }
    }
    String water_temp = "";
    boolean watercycleSprngCode,watercycleSummerCode,watercycleAutumnCode = false,watercycleWinterCode;
    void getPlantInformation(final String q1){
        try {
            Log.d(TAG,"검색할 컨텐츠 번호 => "+q1);

            StringBuilder urlBuilder = new StringBuilder("http://api.nongsaro.go.kr/service/garden/gardenDtl");
            urlBuilder.append("?" + URLEncoder.encode("apiKey","UTF-8") + "="+APIKEY); //공공데이터포털에서 받은 인증키
            urlBuilder.append("&" + URLEncoder.encode("cntntsNo","UTF-8") + "=" + URLEncoder.encode(q1,"UTF-8"));


            URL url = new URL(urlBuilder.toString());
            Log.d(TAG,"URI 주소 ====>"+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");


            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            try{
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(rd);

                int eventType = xmlPullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT){
                    
                    switch (eventType){

                        case XmlPullParser.START_DOCUMENT:{
                            Log.d("PlantInfoActivity","API 파싱 ===> 시작");
                            break;
                        }


                        case XmlPullParser.START_TAG:{
                            String string = xmlPullParser.getName();
                            if(string.equals("watercycleAutumnCode")){
                                watercycleAutumnCode = true;
                            }
                            break;
                        }

                        case XmlPullParser.TEXT:{
                            if(watercycleAutumnCode){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"유지습도 코드 => "+s );
                                water_temp = s;
                                watercycleAutumnCode = false;
                            }
                            break;
                        }
                    }
                    eventType = xmlPullParser.next();

                }

            }catch (XmlPullParserException e){
                Log.d(TAG,"API 파싱 실패 ===> "+ e.getMessage());
            }

            rd.close();
            conn.disconnect();

            Log.d(TAG,"API 파싱 ===> 끝");


        } catch (Exception e) {
            Log.d(TAG,"API 파싱 실패 ===> "+ e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_regi){
            final String plant_kind = txt_kindplant.getText().toString();
            if(plant_kind.length()<1){
                Toast.makeText(getApplicationContext(),"식물 종류를 입력해주세요!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(serial_device == 0){
                Toast.makeText(getApplicationContext(),"급수 기기와 연결해주세요!",Toast.LENGTH_SHORT).show();
                return;
            }


            //토양의 종류
            spinner_soil = findViewById(R.id.spinner_soil);
            final int soil_kind = spinner_soil.getSelectedItemPosition();

            //등록버튼 클릭 당시 날짜를 받아서 저장함
            final String reg_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if(plantcntnts.getHdCode() > 100 || plantcntnts.getHdCode() < 1){
                Log.d(TAG,"이 식물의 유지습도는 "+ plantcntnts.getHdCode()+"입니다");
                Toast.makeText(getApplicationContext(),"잠시만 기다려주세요!",Toast.LENGTH_SHORT).show();
                return;
            }

            onViewPrograss(true);

            PlantToServer plantToServer = new PlantToServer(plantcntnts.getCntntsSj(), plantcntnts.getHdCode(),serial_device,getApplicationContext());
            API api_ = RetrofitService.postServer().create(API.class);
            Call<ResponseBody> call = api_.postUserDevice(plantToServer);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200){
                        if(response.body()!=null){
                            try {
                                String body = response.body().string();
                                Uri uri = new Uri.Builder().build().parse(LIST_URI);
                                if(uri!=null) {
                                    ContentValues values = new ContentValues();
                                    values.put("kind", plantcntnts.getCntntsSj());
                                    values.put("date", reg_date);
                                    values.put("soil", soil_kind);
                                    values.put("num", plantcntnts.getCntntsNo());
                                    values.put("water", plantcntnts.getHdCode());

                                    if(plant_img!=null && path!=null){
                                        values.put("image", plant_img);
                                        values.put("path", path);
                                    }

                                    uri = getContentResolver().insert(uri,values);
                                    Log.d("데이터베이스;식물리스트",  "INSERT 결과 =>"+uri);
                                }
                                onViewPrograss(false);
                                Toast.makeText(getApplicationContext(),"식물이 정상적으로 등록되었습니다.",Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();

                            } catch (IOException e) {
                                e.printStackTrace();
                                onViewPrograss(false);
                            }
                        }
                    }else {
                        onViewPrograss(false);
                        Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    onViewPrograss(false);
                    Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                }
            });

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
