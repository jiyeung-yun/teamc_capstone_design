package com.capstone.plantplant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.model.PInfo;
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

import static com.capstone.plantplant.ListActivity.APIKEY;
import static com.capstone.plantplant.ListActivity.LIST_URI;
import static com.capstone.plantplant.db.ListDBHelper.ALL_COLUMS;

public class PlantInfoActivity extends AppCompatActivity {
    private final String TAG ="PlantInfoActivity";

    ImageButton btn_info_close;

    SegmentedGroup btn_info_title;
    LinearLayout ly_soil_information,ly_plant_information;

    TextView txt_plantinfo_kind,txt_plantinfo_soil;

    TextView txt_fmlCodeNm, txt_orgplceInfo, txt_managedemanddo, txt_prpgtEraInfo, txt_grwhTp,
            txt_hdCode, txt_adviseInfo, txt_frtlzrInfo, txt_fncltyInfo;

    LinearLayout ly_fmlCodeNm, ly_orgplceInfo, ly_managedemanddo, ly_prpgtEraInfo, ly_grwhTp,
            ly_hdCode, ly_adviseInfo, ly_frtlzrInfo, ly_fncltyInfo;
    TextView txt_soil_produce,txt_soil_usage,txt_soil_feature,txt_plantinfo_type;

    int index;
    String plant_kind,soil_kind;

    Spinner spinner_select_soil;

    PInfo pInfo;

    //레이아웃 데이터 초기화
    private void InitfindViewByID(){
        ly_fmlCodeNm = findViewById(R.id.ly_fmlCodeNm);
        ly_orgplceInfo = findViewById(R.id.ly_orgplceInfo);
        ly_managedemanddo = findViewById(R.id.ly_managedemanddo);
        ly_prpgtEraInfo = findViewById(R.id.ly_prpgtEraInfo);
        ly_grwhTp = findViewById(R.id.ly_grwhTp);
        ly_hdCode = findViewById(R.id.ly_hdCode);
        ly_frtlzrInfo = findViewById(R.id.ly_frtlzrInfo);
        ly_fncltyInfo = findViewById(R.id.ly_fncltyInfo);

        txt_fmlCodeNm = findViewById(R.id.txt_fmlCodeNm);
        txt_orgplceInfo = findViewById(R.id.txt_orgplceInfo);
        txt_managedemanddo = findViewById(R.id.txt_managedemanddo);
        txt_prpgtEraInfo = findViewById(R.id.txt_prpgtEraInfo);
        txt_grwhTp = findViewById(R.id.txt_grwhTp);
        txt_hdCode = findViewById(R.id.txt_hdCode);
        txt_frtlzrInfo = findViewById(R.id.txt_frtlzrInfo);
        txt_fncltyInfo = findViewById(R.id.txt_fncltyInfo);

        txt_soil_produce = findViewById(R.id.txt_soil_produce);
        txt_soil_usage = findViewById(R.id.txt_soil_usage);
        txt_soil_feature = findViewById(R.id.txt_soil_feature);
        txt_plantinfo_type = findViewById(R.id.txt_plantinfo_type);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

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
        index = intent.getIntExtra("index",0);
        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        if(uri!=null){
            pInfo = new PInfo();
            Cursor cursor = getContentResolver().query(uri,ALL_COLUMS,"_index="+index,null,null);
            int count =  cursor.getCount();
            if(count > 0){
                while(cursor.moveToNext()){
                    plant_kind = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[1]));
                    //도감번호
                    final String plant_idx = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[5]));
                    if(plant_idx==null){
                        Toast.makeText(getApplicationContext(),"식물 정보를 불러오던 중 오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                    }else {
                        //API 통신을 통해 식물에 대한 정보를 받아 제공
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                getPlantInformation(plant_idx);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        String fml = pInfo.getFmlCodeNm();
                                        if(fml != null && !fml.equals(" ") || !fml.equals("")){
                                            ly_fmlCodeNm.setVisibility(View.VISIBLE);
                                            txt_fmlCodeNm.setText(fml);
                                        }else {
                                            ly_fmlCodeNm.setVisibility(View.GONE);
                                        }

                                        String org = pInfo.getOrgplceInfo();
                                        if(org != null && !org.equals(" ") || !org.equals("")){
                                            ly_orgplceInfo.setVisibility(View.VISIBLE);
                                            txt_orgplceInfo.setText(org);
                                        }else {
                                            ly_orgplceInfo.setVisibility(View.GONE);
                                        }

                                        String prp = pInfo.getPrpgtEraInfo();
                                        if(prp != null && !prp.equals(" ") || !prp.equals("")){
                                            ly_prpgtEraInfo.setVisibility(View.VISIBLE);
                                            txt_prpgtEraInfo.setText(prp);
                                        }else {
                                            ly_prpgtEraInfo.setVisibility(View.GONE);
                                        }

                                        String grw = pInfo.getGrwhTp();
                                        if(grw != null && !grw.equals(" ") || !grw.equals("")){
                                            ly_grwhTp.setVisibility(View.VISIBLE);
                                            txt_grwhTp.setText(grw);
                                        }else {
                                            ly_grwhTp.setVisibility(View.GONE);
                                        }

                                        String hdc = pInfo.getHdCode();
                                        if(hdc != null && !hdc.equals(" ") || !hdc.equals("")){
                                            ly_hdCode.setVisibility(View.VISIBLE);
                                            txt_hdCode.setText(hdc);
                                        }else {
                                            ly_hdCode.setVisibility(View.GONE);
                                        }

                                        String frt = pInfo.getFrtlzrInfo();
                                        if(frt != null && !frt.equals(" ") || !frt.equals("")){
                                            ly_frtlzrInfo.setVisibility(View.VISIBLE);
                                            txt_frtlzrInfo.setText(frt);
                                        }else {
                                            ly_frtlzrInfo.setVisibility(View.GONE);
                                        }

                                        String fnc = pInfo.getFncltyInfo();
                                        if(fnc != null && !fnc.equals(" ") || !fnc.equals("")){
                                            ly_fncltyInfo.setVisibility(View.VISIBLE);
                                            txt_fncltyInfo.setText(fnc);
                                        }else {
                                            ly_fncltyInfo.setVisibility(View.GONE);
                                        }

                                        String manag = pInfo.getManagedemanddo();
                                        if(manag != null && !manag.equals(" ") || !manag.equals("")){
                                            ly_managedemanddo.setVisibility(View.VISIBLE);
                                            txt_managedemanddo.setText(manag);
                                        }else {
                                            ly_managedemanddo.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            }
                        }).start();
                    }
                }
            }
            cursor.close();
        }else {
            Toast.makeText(getApplicationContext(),"식물 정보를 불러오던 중 오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
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

    boolean fmlCodeNm = false,orgplceInfo = false,prpgtEraInfo = false,grwhTpCodeNm = false,
            hdCodeNm = false,frtlzrInfo = false,fncltyInfo = false,managedemanddoCodeNm = false;

    void getPlantInformation(final String q1){
        try {
            Log.d("PlantInfoActivity","검색할 컨텐츠 번호 => "+q1);

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
                            if(string.equals("fmlCodeNm")){
                                fmlCodeNm = true;
                            }
                            else if(string.equals("orgplceInfo")){
                                orgplceInfo = true;
                            }
                            else if(string.equals("prpgtEraInfo")){
                                prpgtEraInfo = true;
                            }
                            else if(string.equals("grwhTpCodeNm")){
                                grwhTpCodeNm = true;
                            }
                            else if(string.equals("hdCodeNm")){
                                hdCodeNm = true;
                            }
                            else if(string.equals("frtlzrInfo")){
                                frtlzrInfo = true;
                            }
                            else if(string.equals("fncltyInfo")){
                                fncltyInfo = true;
                            }
                            else if(string.equals("managedemanddoCodeNm")){
                                managedemanddoCodeNm = true;
                            }
                            break;
                        }

                        case XmlPullParser.TEXT:{
                            if(fmlCodeNm){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"과 코드명 => "+s );
                                pInfo.setFmlCodeNm(s);
                                fmlCodeNm = false;

                            }
                            else if(orgplceInfo){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"원산지 정보 => "+s );
                                pInfo.setOrgplceInfo(s);
                                orgplceInfo = false;

                            }
                            else if(prpgtEraInfo){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"번식 시기 정보 => "+s );
                                pInfo.setPrpgtEraInfo(s);
                                prpgtEraInfo = false;

                            }
                            else if(grwhTpCodeNm){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"생육 온도 코드명 => "+s );
                                pInfo.setGrwhTp(s);
                                grwhTpCodeNm = false;

                            }
                            else if(hdCodeNm){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"습도 코드명 => "+s );
                                pInfo.setHdCode(s);
                                hdCodeNm = false;

                            }
                            else if(frtlzrInfo){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"비료 정보 => "+s );
                                pInfo.setFrtlzrInfo(s);
                                frtlzrInfo = false;

                            }
                            else if(fncltyInfo){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"기능성 정보보 =>"+s );
                                pInfo.setFncltyInfo(s);
                                fncltyInfo = false;

                            }
                            else if(managedemanddoCodeNm){
                                String s = xmlPullParser.getText();
                                Log.d(TAG,"관리요구도 코드명 => "+s );
                                pInfo.setManagedemanddo(s);
                                managedemanddoCodeNm = false;

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

}
