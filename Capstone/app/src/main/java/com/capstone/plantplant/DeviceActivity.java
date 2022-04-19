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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.DeviceAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.network.API;
import com.capstone.plantplant.network.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceActivity extends AppCompatActivity {

    ImageButton btn_info_close5;
    RecyclerView ry_device_list;
    ArrayList<Integer> items = new ArrayList<>();
    DeviceAdapter deviceAdapter;

    FrameLayout fy_dv_progressBar;
    ProgressBar progressBar_dv;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_device);

        btn_info_close5 = findViewById(R.id.btn_info_close5);
        btn_info_close5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
            }
        });

        ry_device_list = findViewById(R.id.ry_device_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ry_device_list.setLayoutManager(layoutManager);

        deviceAdapter = new DeviceAdapter(items);
        deviceAdapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                if(position<items.size()){
                    Intent intent = new Intent(getApplicationContext(),RegiPlantActivity.class);
                    int s = items.get(position);
                    intent.putExtra("result_device_num",s);
                    setResult(RESULT_OK,intent);
                    finish();
                    overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
                }
            }
        });
        ry_device_list.setAdapter(deviceAdapter);

        fy_dv_progressBar = findViewById(R.id.fy_dv_progressBar);
        progressBar_dv = findViewById(R.id.progressBar_dv);
        onViewPrograss(true);
        getAPI();

    }
    private void onViewPrograss(boolean view){
        progressBar_dv.setIndeterminate(view);

        if(view){
            fy_dv_progressBar.setVisibility(View.VISIBLE);
        }else{
            fy_dv_progressBar.setVisibility(View.GONE);
        }
    }
    //서버에 등록된 기기목록을 받는 함수
    private void getAPI(){
        API api_ = RetrofitService.getServer().create(API.class);
        Call<ResponseBody> call = api_.getDeviceList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    if(response.body()!=null){
                        try {
                            String body = response.body().string();
                            JSONObject body_ = new JSONObject(body);
                            String item_body = body_.getString("body");
                            JSONObject item_body_ = new JSONObject(item_body);
                            String items_body = item_body_.getString("Items");
                            JSONArray item_array = new JSONArray(items_body);

                            for(int i = 0 ; i< item_array.length();i++){
                                JSONObject item = (JSONObject) item_array.get(i);

                                int device = item.getInt("device_id");
                                String plant_name = item.getString("plant_name");

                                if(plant_name.equals("none")) {
                                    items.add(device);
                                }
                            }
                            if(items.size() < 1){
                                Toast.makeText(getApplicationContext(),"서버에 연동가능한 기기가 없습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                ry_device_list.getAdapter().notifyDataSetChanged();
                            }
                            onViewPrograss(false);

                        } catch (IOException |JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if(response.errorBody()!=null){
                        try {
                            String errormessage = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    onViewPrograss(false);

                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 문제가 생겼습니다...",Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
            }
        });
    }
}
