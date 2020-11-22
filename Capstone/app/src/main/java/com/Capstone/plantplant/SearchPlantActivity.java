package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.KindSearchAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchPlantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ImageButton btn_info_close3;
    SearchView search_kind;

    ArrayList<String> items = new ArrayList<>();
    RecyclerView ry_search_list;
    KindSearchAdapter kindSearchAdapter;
    ProgressBar progressBar_plant;
    FrameLayout fy_progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        /*상단 작업표시줄 투명하게 만드는 코드*/

        setContentView(R.layout.activity_search_plant);

        //닫기 버튼
        btn_info_close3 = findViewById(R.id.btn_info_close3);
        btn_info_close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                loadingPrograss(false);
                finish();
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

        kindSearchAdapter = new KindSearchAdapter(items);
        kindSearchAdapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                if(position<items.size()){
                    String result = items.get(position);
                    Intent intent = new Intent(getApplicationContext(),RegiPlantActivity.class);
                    intent.putExtra("result_kind",result);
                    setResult(RESULT_OK,intent);
                    loadingPrograss(false);
                    finish();
                }
            }
        });
        kindSearchAdapter.addItem("검색어 결과가 보여지는 곳 입니다.");
        ry_search_list.setAdapter(kindSearchAdapter);

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

    final String ServiceKey = "Xzd9L81I4P%2F%2FI6OaxEbY9FmvA5KUOJDEsk82pe396jZY0MfLk0IQn1BYbpv1JYnxu4kZ7pRf38PjCqsaOd2DwQ%3D%3D"; //인증키

    //한 페이지에 아이템 갯수
    int numOfRows = 100;
   //검색어 관련 전체 폐이지갯수
    int totalPageCount = 0;

    //태그 확인
    boolean systemkorname = false;
    boolean totalcount = false;
    void getXmlData(String str,int pageNo){
        try {
            Log.d("API DATA PARSING","검색어  => "+str);

            StringBuilder urlBuilder = new StringBuilder("http://openapi.nature.go.kr/openapi/service/rest/KpniService/systemSearch");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ServiceKey); //공공데이터포털에서 받은 인증키
            urlBuilder.append("&" + URLEncoder.encode("st","UTF-8") + "=" + 1); //검색어 구분 (st = 1 : 분류군국문명)
            urlBuilder.append("&" + URLEncoder.encode("sw","UTF-8") + "=" + URLEncoder.encode(str, "UTF-8")); // 검색어
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + numOfRows); // 한 페이지 결과 수
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + pageNo); //페이지 번호

            URL url = new URL(urlBuilder.toString());
            Log.d("API DATA PARSING","URI 주소 =>"+url);

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
                            Log.d("API DATA PARSING","API 파싱 => 성공");
                       break;
                        }
                        case XmlPullParser.START_TAG:{
                            String string = xmlPullParser.getName();
                            if(string.equals("item")){
                                Log.d("API DATA PARSING","--------------------<item>--------------------");
                            }
                            if(string.equals("systemkorname")){
                                systemkorname = true;
                            }
                            if(string.equals("totalCount")){
                                totalcount = true;
                            }
                            break;
                        }
                        case XmlPullParser.TEXT:{
                            if(systemkorname){
                                String s = xmlPullParser.getText();
                                items.add(s);
                                Log.d("API DATA PARSING","국문명 => "+s);
                                systemkorname = false;
                            }
                            if(totalcount){
                                String s = xmlPullParser.getText();
                                Log.d("API DATA PARSING","전체 아이템 갯수 => "+s);
                                //전체 카운트
                                int totalCount = Integer.parseInt(s);
                                totalPageCount = totalCount/numOfRows;
                                totalcount = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            if(xmlPullParser.getName().equals("item")){
                            }
                            break;
                        }
                    }
                    eventType = xmlPullParser.next();
                }
            }catch (XmlPullParserException e){
                Log.d("API DATA PARSING","API 파싱 실패=> "+ e.getMessage());
            }
            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            Log.d("API DATA PARSING","API 파싱 실패=> "+ e.getMessage());

        }
    }
    int pageNo = 0;
    @Override
    public boolean onQueryTextSubmit(final String query) {
        loadingPrograss(true);

        pageNo = 1;
        totalPageCount = 0;
        if(kindSearchAdapter!=null){
            items.clear();
            kindSearchAdapter.clear();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getXmlData(query,pageNo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                    }
                });

            }
        }).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                assert ry_search_list!=null;
                ry_search_list.getAdapter().notifyDataSetChanged();
                loadingPrograss(false);
            }
        },2000);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
