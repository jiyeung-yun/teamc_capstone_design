package com.capstone.plantplant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.ItemListAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.model.ListItem;
import com.capstone.plantplant.model.Plantcntnts;
import com.capstone.plantplant.network.API;
import com.capstone.plantplant.network.RetrofitService;
import com.capstone.plantplant.util.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {
    static final String APIKEY = "20201230U305ZJZS6YJFNRMABTDW"; //인증키
    public static final String LIST_URI = "content://com.capstone.plantplant/list";
    private final int REGI_PLANT_REQUEST_CODE = 2011;
    public static final String CHANNEL_ID = "growplant";

    ArrayList<ListItem> items = new ArrayList<>();

    LinearLayout btn_add_item;
    RecyclerView ry_plant_list;
    ItemListAdapter adapter;
    TextView txt_list_title,txt_list_content;

    String alarmTitle = "[알림] 꽃부기에서 알려드립니다!";
    final String alarmServer = "서버에 저장된 데이터를 불러오는 중 입니다.";
    final String alarmContent = "원하는 식물을 등록하고 관리해보세요!\n실시간으로 화분의 정보를 받으실 수 있습니다.";

    @Override
    protected void onStart() {
        super.onStart();

        //식물리스트 새로고침 매소드
        refreshPlantList();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        String username = new PreferenceManager().getString(getApplicationContext(), "USER_NAME");
        if(username!=null && !username.equals("")){
            alarmTitle =  "[알림] 꽃부기에서 "+username+"님께 알려드립니다";
            Toast.makeText(getApplicationContext(),username+"님 안녕하세요!",Toast.LENGTH_SHORT).show();
        }

        txt_list_title = findViewById(R.id.txt_list_title);
        txt_list_title.setText(alarmTitle);
        txt_list_content = findViewById(R.id.txt_list_content);

        //식물리스트에 식물아이템을 추가하는 버튼
        btn_add_item = findViewById(R.id.btn_add_item);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(getApplicationContext(), RegiPlantActivity.class);
                startActivityForResult(mintent,REGI_PLANT_REQUEST_CODE);
            }
        });


        //식물 리스트 구성 초기화
        ry_plant_list = findViewById(R.id.ry_plant_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ry_plant_list.setLayoutManager(layoutManager);

        adapter = new ItemListAdapter();
        adapter.setContext(getApplicationContext());
        adapter.setItems(items);
        adapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(),ItemActivity.class);
                int index =  adapter.getItem(position).getIndex();
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });
        ry_plant_list.setAdapter(adapter);

    }


    //식물리스트 새로고침 매소드
    private void refreshPlantList(){
        if(items!=null && adapter!=null){
            items.clear();
            adapter.clear();
        }
        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        if(uri!=null){

            String[] colums = {"_index","kind","image","path"};
            Cursor cursor = getContentResolver().query(uri,colums,null,null,null);

            int count =  cursor.getCount();
            if(count > 0){
                while(cursor.moveToNext()){
                    ListItem item = new ListItem();
                    //식별자
                    int idx = cursor.getInt(cursor.getColumnIndex(colums[0]));
                    item.setIndex(idx);

                    //식물 종류
                    String kind = cursor.getString(cursor.getColumnIndex(colums[1]));
                    item.setName(kind);

                    String filename =  cursor.getString(cursor.getColumnIndex(colums[2]));
                    item.setFilename(filename);

                    String path =  cursor.getString(cursor.getColumnIndex(colums[3]));
                    item.setPath(path);

                    items.add(item);

                }
                ry_plant_list.getAdapter().notifyDataSetChanged();
            }
            cursor.close();
        }

        //식물 센서 정보만
        getListData();
    }
    NotificationManager notificationManager = null;

    private void getListData(){
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        txt_list_content.setText(alarmServer);
        notificationManager.cancelAll();

        API api_ = RetrofitService.getServer().create(API.class);
        Call<ResponseBody> call = api_.getAllData();
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
                                return;
                            }
                            JSONObject item_body_ = new JSONObject(item_body);
                            String item_body2 = item_body_.getString("Items");
                            JSONArray item_array = new JSONArray(item_body2);

                            for(int i = 0 ; i< item_array.length();i++){
                                JSONObject item = (JSONObject) item_array.get(i);
                                String plantName = item.getString("plant_name");
                                int SensorWater = item.getInt("waterlevel_sensor");
                                int standard_humidity = item.getInt("standard_humidity");
                                int idx = setItem(plantName, SensorWater == 1);

                                if(SensorWater==0 && idx!=99999 && standard_humidity!=0){
                                   onNotification(plantName,idx);
                                }
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    txt_list_content.setText(alarmContent);
                }else{
                    txt_list_content.setText("서버와의 연결에 실패하였습니다.");
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                txt_list_content.setText("서버와의 연결에 실패하였습니다.");
            }
        });
    }
    public void onNotification(String plantName, int index) {

        Intent notificationIntent = new Intent(this, ItemActivity.class);
        notificationIntent.putExtra("index",index+1);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_format_color_reset_24px)) //BitMap 이미지 요구
                .setContentTitle("[경고]물을 채워주세요!")
                .setContentText(plantName+"에 연결된 물탱크에 물이 부족해요:(")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_format_color_reset_24px); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(index, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }

     private int setItem(String plantName, boolean waterSenser){
        for(int i=0;i<items.size();i++){
            ListItem item = items.get(i);
            if(plantName.equals(item.getName())){
                items.get(i).setProblem(!waterSenser);
                ry_plant_list.getAdapter().notifyDataSetChanged();
                return i;
            }
        }
        return 99999;
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGI_PLANT_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                refreshPlantList();
            }
        }
    }

}
