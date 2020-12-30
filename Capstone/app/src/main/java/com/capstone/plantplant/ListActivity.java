package com.capstone.plantplant;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.ItemListAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.model.ItemList;
import com.capstone.plantplant.model.Plant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    public static final String LIST_URI = "content://com.capstone.plantplant/list";
    private final int REGI_PLANT_REQUEST_CODE = 2011;

    public static List<Plant> plantList;

    FloatingActionButton btn_add_item;
    RecyclerView ry_plant_list;

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

    }


    //식물리스트 새로고침 매소드
    private void refreshPlantList(){

        if(ry_plant_list.getAdapter()!=null){
            ry_plant_list.setAdapter(null);
        }
        final ItemListAdapter adapter = new ItemListAdapter();
        adapter.setOnItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(),ItemActivity.class);
                int index =  adapter.getItem(position).getIndex();
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });

        Uri uri = new Uri.Builder().build().parse(LIST_URI);
        if(uri!=null){

            String[] colums = {"_index","kind","image","path"};
            Cursor cursor = getContentResolver().query(uri,colums,null,null,null);

            int count =  cursor.getCount();
            if(count > 0){
                while(cursor.moveToNext()){
                    ItemList item = new ItemList();
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

                    adapter.addItem(item);

                }
            }
            cursor.close();
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
