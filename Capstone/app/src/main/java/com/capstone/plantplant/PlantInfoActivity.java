package com.capstone.plantplant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.model.Soil;
import com.capstone.plantplant.db.SoilDBAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

import static com.capstone.plantplant.ListActivity.LIST_URI;

public class PlantInfoActivity extends AppCompatActivity {
    ImageButton btn_info_close;

    SegmentedGroup btn_info_title;
    LinearLayout ly_soil_information,ly_plant_information;

    TextView txt_plantinfo_kind,txt_plantinfo_soil;

    TextView txt_brdMthdDesc,txt_farmSpftDesc,txt_grwEvrntDesc
                            ,txt_smlrPlntDesc,txt_useMthdDesc;
    TextView txt_soil_produce,txt_soil_usage,txt_soil_feature,txt_plantinfo_type;

    String plant_kind , soil_kind;

    Spinner spinner_select_soil;

    //레이아웃 데이터 초기화
    private void InitfindViewByID(){
        txt_brdMthdDesc = findViewById(R.id.txt_brdMthdDesc);
        txt_brdMthdDesc.setMovementMethod(new ScrollingMovementMethod());

        txt_farmSpftDesc = findViewById(R.id.txt_farmSpftDesc);
        txt_farmSpftDesc.setMovementMethod(new ScrollingMovementMethod());

        txt_grwEvrntDesc = findViewById(R.id.txt_grwEvrntDesc);
        txt_grwEvrntDesc.setMovementMethod(new ScrollingMovementMethod());

        txt_smlrPlntDesc = findViewById(R.id.txt_smlrPlntDesc);
        txt_smlrPlntDesc.setMovementMethod(new ScrollingMovementMethod());

        txt_useMthdDesc = findViewById(R.id.txt_useMthdDesc);
        txt_useMthdDesc.setMovementMethod(new ScrollingMovementMethod());


        txt_soil_produce = findViewById(R.id.txt_soil_produce);
        txt_soil_produce.setMovementMethod(new ScrollingMovementMethod());

        txt_soil_usage = findViewById(R.id.txt_soil_usage);
        txt_soil_usage.setMovementMethod(new ScrollingMovementMethod());

        txt_soil_feature = findViewById(R.id.txt_soil_feature);
        txt_soil_feature.setMovementMethod(new ScrollingMovementMethod());

        txt_plantinfo_type = findViewById(R.id.txt_plantinfo_type);
        txt_plantinfo_type.setMovementMethod(new ScrollingMovementMethod());

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        /*상단 작업표시줄 투명하게 만드는 코드*/

        setContentView(R.layout.activtiy_plantinfo);
        //레이아웃 데이터 초기화
        InitfindViewByID();
        //데이터베이스 초기화
        initLoadDB();

        btn_info_close = findViewById(R.id.btn_info_close);
        btn_info_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ly_soil_information = findViewById(R.id.ly_soil_information);
        ly_plant_information = findViewById(R.id.ly_plant_information);

        btn_info_title = findViewById(R.id.btn_info_title);
        btn_info_title.setTintColor(Color.parseColor("#FF508221"), Color.parseColor("#FFFFFFFF"));
        btn_info_title.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.btn_plant_info:{
                        ly_soil_information.setVisibility(View.GONE);
                        ly_plant_information.setVisibility(View.VISIBLE);
                        break;
                    }
                    case R.id.btn_soil_info:{
                        ly_soil_information.setVisibility(View.VISIBLE);
                        ly_plant_information.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        });
        btn_info_title.check(R.id.btn_plant_info);

        Intent intent = getIntent();
        plant_kind = intent.getStringExtra("plant_kind");

        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        if(uri!=null){
            String[] colums = {"num"};
            Cursor cursor = getContentResolver().query(uri,colums,"kind='"+plant_kind+"'",null,null);
            int count =  cursor.getCount();
            if(count > 0){
                while(cursor.moveToNext()){
                    //도감번호
                    int plant_idx = cursor.getInt(cursor.getColumnIndex(colums[0]));

                    //API 통신을 통해 식물에 대한 정보를 받아 제공
                    LoadPlantInformation(plant_idx);
                }
            }
            cursor.close();
        }

        txt_plantinfo_kind = findViewById(R.id.txt_plantinfo_kind);
        txt_plantinfo_kind.setText(plant_kind);

        soil_kind = intent.getStringExtra("soil_kind");

        spinner_select_soil = findViewById(R.id.spinner_select_soil);


        final ArrayList<String> arrayList = new ArrayList<>();
        //데이터 베이스 토양 종류 속성 값을 배열에 저장
        arrayList.add("혼합 인공 배양 상토");
        for(int i=0;i<soils.size();i++){
            arrayList.add(soils.get(i).getSname());
        }

        spinner_select_soil.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList));
        spinner_select_soil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    String kind = arrayList.get(position);
                    LoadSoilInformation(kind);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String[] arr = getResources().getStringArray(R.array.soil_array);
        if(soil_kind.equals(arr[0])){

            spinner_select_soil.setVisibility(View.VISIBLE);
        }else {

            spinner_select_soil.setVisibility(View.GONE);
            //토양 정보를 DB에서 불러서 제공
            LoadSoilInformation(soil_kind);

        }


    }

    //데이터베이스에 저장된 토양 관련 데이터 전부 List에 저장
    List<Soil> soils;


    private void initLoadDB() {
        SoilDBAdapter mDbHelper = new SoilDBAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        soils = mDbHelper.getTableData();

        // DB 닫기
        mDbHelper.close();
    }




    //토양 정보 DB에서 로드
    private void LoadSoilInformation(String soild_name){

        txt_plantinfo_soil = findViewById(R.id.txt_plantinfo_soil);
        txt_plantinfo_soil.setText(soild_name);

        for(int i=0;i<soils.size();i++){
            if(soils.get(i).getSname().equals(soild_name)){

                txt_plantinfo_type.setText(soils.get(i).getStype());

                txt_soil_produce.setText(soils.get(i).getSproduce());

                txt_soil_usage.setText(soils.get(i).getSusage());

                txt_soil_feature.setText(soils.get(i).getScharacter());

                break;
            }
        }

    }




    //식물 종류에 따른 종류를 api에서 응답받아 로드
    private void LoadPlantInformation(final int plant_idx){
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getPlantInformation(plant_idx);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                    }
                });

            }
        }).start();

    }



    //공공데이터포털에서 받은 인증키
    final String SERVICE_KEY = "Xzd9L81I4P%2F%2FI6OaxEbY9FmvA5KUOJDEsk82pe396jZY0MfLk0IQn1BYbpv1JYnxu4kZ7pRf38PjCqsaOd2DwQ%3D%3D";

    boolean brdMthdDesc = false,farmSpftDesc = false,grwEvrntDesc=false,smlrPlntDesc = false,useMthdDesc = false;

    void getPlantInformation(final int q1){
        try {
            Log.d("PlantInfoActivity","검색할 도감번호 => "+q1);

            StringBuilder urlBuilder = new StringBuilder("http://openapi.nature.go.kr/openapi/service/rest/PlantService/plntIlstrInfo");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+SERVICE_KEY);
            urlBuilder.append("&" + URLEncoder.encode("q1","UTF-8")+"="+ q1);

            URL url = new URL(urlBuilder.toString());
            Log.d("PlantInfoActivity","URI 주소 =>"+url);
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
                            Log.d("PlantInfoActivity","API 파싱 => 시작");
                            break;
                        }


                        case XmlPullParser.START_TAG:{
                            String string = xmlPullParser.getName();
                            //번식방법
                            if(string.equals("brdMthdDesc")){
                                brdMthdDesc = true;
                            }
                            //재배특성
                            else if(string.equals("farmSpftDesc")){
                                farmSpftDesc = true;
                            }
                            //생육환경설명
                            else if(string.equals("grwEvrntDesc")){
                                grwEvrntDesc = true;
                            }
                            //유사식물설명
                            else if(string.equals("smlrPlntDesc")){
                                smlrPlntDesc = true;
                            }
                            //사용법
                            else if(string.equals("useMthdDesc")){
                                useMthdDesc = true;
                            }
                            break;
                        }


                        case XmlPullParser.TEXT:{
                            //번식방법
                            if(brdMthdDesc){
                                String s = xmlPullParser.getText();
                                Log.d("PlantInfoActivity","번식방법 => "+s );
                                if(!s.equals(" ")){
                                    String st = txt_brdMthdDesc.getText().toString()+s;
                                    txt_brdMthdDesc.setText(st);
                                }
                            }
                            //재배특성
                            else if(farmSpftDesc){
                                String s = xmlPullParser.getText();
                                Log.d("PlantInfoActivity","재배특성 => "+s );
                                if(!s.equals(" ")){
                                    String st = txt_farmSpftDesc.getText().toString()+s;
                                    txt_farmSpftDesc.setText(st);
                                }
                            }
                            //생육환경설명
                            else if(grwEvrntDesc){
                                String s = xmlPullParser.getText();
                                if(!s.equals(" ")){
                                    String st = txt_grwEvrntDesc.getText().toString()+s;
                                    txt_grwEvrntDesc.setText(st);
                                }
                            }
                            //유사식물설명
                            else if(smlrPlntDesc){
                                String s = xmlPullParser.getText();
                                Log.d("PlantInfoActivity","유사식물설명 => "+s );
                                if(!s.equals(" ")){
                                    String st = txt_smlrPlntDesc.getText().toString()+s;
                                    txt_smlrPlntDesc.setText(st);
                                }
                            }
                            //사용법
                            else if(useMthdDesc){
                                String s = xmlPullParser.getText();
                                Log.d("PlantInfoActivity","사용법 => "+s );
                                if(!s.equals(" ")){
                                    String st = txt_useMthdDesc.getText().toString()+s;
                                    txt_useMthdDesc.setText(st);
                                }
                            }
                            break;
                        }


                        case XmlPullParser.END_TAG:{
                            String string = xmlPullParser.getName();
                            //번식방법
                            if(string.equals("brdMthdDesc")){
                                brdMthdDesc = false;
                            }
                            //재배특성
                            else if(string.equals("farmSpftDesc")){
                                farmSpftDesc = false;
                            }
                            //생육환경설명
                            else if(string.equals("grwEvrntDesc")){
                                grwEvrntDesc = false;
                            }
                            //유사식물설명
                            else if(string.equals("smlrPlntDesc")){
                                smlrPlntDesc = false;
                            }
                            //사용법
                            else if(string.equals("useMthdDesc")){
                                useMthdDesc = false;
                            }
                            break;
                        }
                    }


                    eventType = xmlPullParser.next();


                }

            }catch (XmlPullParserException e){
                Log.d("PlantInfoActivity","API 파싱 실패=> "+ e.getMessage());
            }


            rd.close();
            conn.disconnect();

            Log.d("PlantInfoActivity","API 파싱 => 끝");


        } catch (Exception e) {
            Log.d("PlantInfoActivity","API 파싱 실패=> "+ e.getMessage());
        }

    }

}