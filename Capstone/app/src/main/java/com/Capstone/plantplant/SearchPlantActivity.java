package com.capstone.plantplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.capstone.plantplant.network.API;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchPlantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ImageButton btn_info_close3;
    SearchView search_kind;

    ArrayList<String> items = new ArrayList<>();
    RecyclerView ry_search_list;
    KindSearchAdapter kindSearchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_search_plant);

        btn_info_close3 = findViewById(R.id.btn_info_close3);
        btn_info_close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        search_kind = findViewById(R.id.search_kind);
        search_kind.setIconified(false);
        search_kind.setOnQueryTextListener(this);

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
    InputStream is;
    //식물 종류리스트 불러오기
    private void LoadPlantKindList(String str) throws XmlPullParserException, IOException {
        if(kindSearchAdapter!=null){
            items.clear();
            kindSearchAdapter.clear();
        }
        String apikey = "20201102QJ9WDI6LGLCMHSR5GQOG";
        String queryUrl="http://api.nongsaro.go.kr/service/garden/gardenList/"
                +apikey+"sText="+str;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(queryUrl).
                addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
        API api = retrofit.create(API.class);
        Call<ResponseBody> response = api.LoadAPI();
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplicationContext(),"응답코드 : "+response.code(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
        //OpenAPI openAPI = new OpenAPI(queryUrl);
        //openAPI.execute();
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


    class OpenAPI extends AsyncTask<Void, Void, String> {

        private String queryurl="";

        public OpenAPI(String url) {
            this.queryurl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer buffer=new StringBuffer();

            try{

                URL url = new URL(queryurl);//문자열로 된 요청 url을 URL 객체로 생성.
                is = url.openStream(); //url위치로 입력스트림 연결
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//xml파싱을 위한
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기

                String tag;

                xpp.next();
                int eventType= xpp.getEventType();
                while( eventType != XmlPullParser.END_DOCUMENT ){
                    switch( eventType ){
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            tag= xpp.getName();//테그 이름 얻어오기

                            if(tag.equals("item")) ;
                            else if(tag.equals("cntntsSj")){
                                String name =  xpp.getText().toString();
                                items.add(name);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType= xpp.next();
                }

            } catch (Exception e2){
                e2.printStackTrace();
                Toast.makeText(getApplicationContext(),"조회 실패",Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            assert  ry_search_list.getAdapter()!=null;
            ry_search_list.getAdapter().notifyDataSetChanged();
        }
    }
}
