package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.SearchPlantAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.model.Plantcntnts;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.capstone.plantplant.ListActivity.APIKEY;


public class SearchPlantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ImageButton btn_info_close3;
    SearchView search_kind;

    ArrayList<Plantcntnts> items = new ArrayList<>();

    RecyclerView ry_search_list;
    SearchPlantAdapter searchPlantAdapter;
    ProgressBar progressBar_plant;
    FrameLayout fy_progressBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_searchplant);

        //닫기 버튼
        btn_info_close3 = findViewById(R.id.btn_info_close3);
        btn_info_close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                loadingPrograss(false);
                finish();
                overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
            }
        });

        //식물의 종류를 검색할 수 있는 창
        search_kind = findViewById(R.id.search_kind);
        search_kind.setQueryHint("검색어를 입력해주세요.");
        search_kind.onActionViewExpanded();
        search_kind.setOnQueryTextListener(this);


        //검색된 식물의 결과를 보여주는 리스트
        ry_search_list=findViewById(R.id.ry_search_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ry_search_list.setLayoutManager(layoutManager);

        searchPlantAdapter = new SearchPlantAdapter(items);
        searchPlantAdapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                if(position<items.size()){
                    Intent intent = new Intent(getApplicationContext(),RegiPlantActivity.class);
                    Plantcntnts p = items.get(position);
                    intent.putExtra("result_kind_num",p.getCntntsNo());
                    intent.putExtra("result_kind_name",p.getCntntsSj());

                    setResult(RESULT_OK,intent);
                    loadingPrograss(false);
                    finish();
                    overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
                }
            }
        });
        ry_search_list.setAdapter(searchPlantAdapter);

        //프로그래스 바 초기화
        fy_progressBar = findViewById(R.id.fy_progressBar);
        progressBar_plant = findViewById(R.id.progressBar_plant);
        loadingPrograss(false);
    }
    //xml파일 내 프로그래스바 제어
    private void loadingPrograss(boolean load){
        if(load){
            fy_progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else {
            fy_progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        progressBar_plant.setIndeterminate(load);
    }
    int pageNo = 0;
    @Override
    public boolean onQueryTextSubmit(final String query) {
        loadingPrograss(true);

        if(searchPlantAdapter !=null){
            items.clear();
            searchPlantAdapter.clear();
        }

        pageNo = 1;
        totalPageCount = 0;

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getXmlData(query,pageNo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ry_search_list.getAdapter().notifyDataSetChanged();
                        loadingPrograss(false);
                    }
                });

            }
        }).start();
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


   //검색어 관련 전체 폐이지갯수
    int totalPageCount = 0;

    Plantcntnts p;

    //태그 확인
    boolean cntntsNo = false;
    boolean cntntsSj = false;
    void getXmlData(String str,int pageNo){
        try {
            Log.d("SearchPlantActivity","검색어  => "+str);

            StringBuilder urlBuilder = new StringBuilder("http://api.nongsaro.go.kr/service/garden/gardenList");
            urlBuilder.append("?" + URLEncoder.encode("apiKey","UTF-8") + "="+APIKEY); //공공데이터포털에서 받은 인증키

            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + pageNo); //페이지 번호

            urlBuilder.append("&" + URLEncoder.encode("sType","UTF-8") + "=" + URLEncoder.encode("sCntntsSj","UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("sText","UTF-8") + "=" + URLEncoder.encode(str, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("wordType","UTF-8") + "=" + URLEncoder.encode("cntntsSj", "UTF-8"));



            URL url = new URL(urlBuilder.toString());
            Log.d("SearchPlantActivity","URI 주소 =>"+url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");


            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            else {
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
                            Log.d("SearchPlantActivity","API 파싱 => 성공");
                       break;
                        }
                        case XmlPullParser.START_TAG:{
                            String string = xmlPullParser.getName();
                            if(string.equals("item")){
                                Log.d("API DATA PARSING","--------------------<item>--------------------");
                            }
                            if(string.equals("cntntsNo")){
                                cntntsNo = true;
                            }
                            if(string.equals("cntntsSj")){
                                cntntsSj = true;
                            }
                            break;
                        }
                        case XmlPullParser.TEXT:{
                            if(cntntsNo){
                                String s1 = xmlPullParser.getText();
                                p = new Plantcntnts();
                                p.setCntntsNo(s1);
                                Log.d("SearchPlantActivity","시리얼넘버 => "+s1);
                                cntntsNo = false;
                            }
                            if(cntntsSj){
                                String s2 = xmlPullParser.getText();
                                p.setCntntsSj(s2);

                                Log.d("SearchPlantActivity","이름 => "+s2);
                                cntntsSj = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            if(xmlPullParser.getName().equals("item")){
                                items.add(p);
                            }
                            break;
                        }
                    }


                    eventType = xmlPullParser.next();

                }

            }catch (XmlPullParserException e){
                Log.d("SearchPlantActivity","API 파싱 실패=> "+ e.getMessage());
            }

            rd.close();
            conn.disconnect();
            Log.d("SearchPlantActivity","API 파싱 성공 ");

        } catch (Exception e) {
            Log.d("SearchPlantActivity","API 파싱 실패=> "+ e.getMessage());
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
    }
}
