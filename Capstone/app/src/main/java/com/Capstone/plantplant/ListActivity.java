package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.ItemListAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.model.ItemList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

public class ListActivity extends AppCompatActivity {
    private final int REGI_PLANT_REQUEST_CODE = 2011;

    private final String DEFAULT_VALUE_STRING = "";

    SharedPreferences prefs;

    FloatingActionButton btn_add_item;
    RecyclerView ry_plant_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        btn_add_item = findViewById(R.id.btn_add_item);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(getApplicationContext(), RegiPlantActivity.class);
                startActivityForResult(mintent,REGI_PLANT_REQUEST_CODE);
            }
        });

        ry_plant_list = findViewById(R.id.ry_plant_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ry_plant_list.setLayoutManager(layoutManager);
        refreshPlantList();
    }
    //식물리스트 새로고침 매소드
    private void refreshPlantList(){
        if(ry_plant_list.getAdapter()!=null){
            ry_plant_list.setAdapter(null);
        }
        ItemListAdapter adapter = new ItemListAdapter();
        adapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(),ItemActivity.class);
                Log.d("List Position","선택한 리스트아이템 위치 : "+ (position+1));
                intent.putExtra("count",position+1);
                startActivity(intent);
            }
        });
        prefs = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        int count =  prefs.getInt("register", 0);
        if(count!=0){
            for(int i=1;i<=count;i++){
                ItemList item = new ItemList();
                //식물 종류
                String kind = prefs.getString("plant_kind"+i,DEFAULT_VALUE_STRING);
                item.setName(kind);

                String date = "2020-11-05";
                item.setDate(date);

                adapter.addItem(item);
            }
        }
        ry_plant_list.setAdapter(adapter);
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
