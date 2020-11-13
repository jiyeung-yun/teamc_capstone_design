package com.capstone.plantplant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.ItemListAdapter;
import com.capstone.plantplant.control.OnAdapterItemClickListener;
import com.capstone.plantplant.control.TipAdapter;
import com.capstone.plantplant.model.ItemList;
import com.capstone.plantplant.model.ItemTip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListActivity extends AppCompatActivity {
    public static final String LIST_URI = "content://com.capstone.plantplant/list";
    private final int REGI_PLANT_REQUEST_CODE = 2011;

    LinearLayout ly_information;
    RecyclerView rv_tip;

    FloatingActionButton btn_add_item;
    RecyclerView ry_plant_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ly_information = findViewById(R.id.ly_information);
        ly_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InfoActivity.class);
                startActivity(intent);
            }
        });
        //tip 내용 초기화하는 메소드
        initRecyclerView();

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

        //식물리스트 새로고침 매소드
        refreshPlantList();
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
            String[] colums = {"_index","kind","date"};
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

                    //식물 날짜
                    String date =  cursor.getString(cursor.getColumnIndex(colums[2]));
                    item.setDate(date);

                    adapter.addItem(item);

                }
            }
            cursor.close();
        }

        ry_plant_list.setAdapter(adapter);
    }
    //tip 내용 초기화하는 메소드
    private void initRecyclerView(){
        rv_tip = findViewById(R.id.rv_tip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_tip.setLayoutManager(layoutManager);

        TipAdapter adapter = new TipAdapter();
        adapter.addItem(new ItemTip("물은 적당히!",R.drawable.ic_opacity_24px,
                "화초가 물에 잠기지 않도록 물을 줘야 해요\n화분의 흙은 촉촉한 정도로만 유지해도 됩니다",
                Color.parseColor("#FF6989B2"),"- 1 -"));
        adapter.addItem(new ItemTip("화분의 위치도 중요!",R.drawable.ic_wb_sunny_24px,
                "집안에 비치는 방향을 파악해서 햇볕이 드는 곳, 그늘이 지는 곳, 습도와 온도에 맞춰서 화분을 놓으세요",
                Color.parseColor("#FFF29661"),"- 2 -"));
        adapter.addItem(new ItemTip("생장에 알맞은 환경이 필요",R.drawable.ic_local_florist_24px,
                "분갈이 시 너무 큰 화분에 옮기지 마세요.\n지금 화분의 한 사이즈 큰 것을 사용하시면 됩니다.",
                Color.parseColor("#FF508221"),"- 3 -"));
        rv_tip.setAdapter(adapter);

        //Recyclerview의 연속 스크롤을 막아 주는 헬퍼
        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }
                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(rv_tip);
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
