package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.io.InputStream;
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
                finish();
            }
        });

        //식물의 종류를 검색할 수 있는 창
        search_kind = findViewById(R.id.search_kind);
        search_kind.setIconified(false);
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
                    finish();
                }

            }
        });
        ry_search_list.setAdapter(kindSearchAdapter);
    }
    String data;

    //식물 종류리스트 불러오기
    private void LoadPlantKindList(final String str) {
        if(kindSearchAdapter!=null){
            items.clear();
            kindSearchAdapter.clear();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                data = getXmlData(str);//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                    }
                });

            }
        }).start();



    }

    final String ServiceKey = "Xzd9L81I4P%2F%2FI6OaxEbY9FmvA5KUOJDEsk82pe396jZY0MfLk0IQn1BYbpv1JYnxu4kZ7pRf38PjCqsaOd2DwQ%3D%3D"; //인증키

    //한 페이지에 아이템 갯수
    int numOfRows = 15;
    //로드할 페이지 번호
    int pageNo = 1;

   //검색한 전체 페이지 수
    int totalCount;

    String getXmlData(String str){
        try {
            Log.d("API DATA PARSING","!!**********URL 객체 생성**********");

            StringBuilder urlBuilder = new StringBuilder("http://openapi.nature.go.kr/openapi/service/rest/KpniService/systemSearch?"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ServiceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("인증키(URL Encode)", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
            urlBuilder.append("&" + URLEncoder.encode("q1","UTF-8") + "=" + URLEncoder.encode("str", "UTF-8"));
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        Log.d("API DATA PARSING","!!**********API 파싱 실패**********");

        return null;

    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            LoadPlantKindList(query);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"조회 실패",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
