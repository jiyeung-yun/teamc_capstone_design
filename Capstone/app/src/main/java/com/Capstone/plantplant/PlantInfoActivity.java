package com.capstone.plantplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.control.TipAdapter;
import com.capstone.plantplant.model.ItemTip;

import static com.capstone.plantplant.SplashActivity.PREFERENCES_NAME;

/*
*
* 사용자가 입력한 식물의 종류를 받아서 저장하는 코드 작성
*
* */
public class PlantInfoActivity extends AppCompatActivity {
    private final String DEFAULT_VALUE_STRING = "";

    ImageButton btn_info_close;
    TextView txt_plantinfo_kind,txt_plantinfo_soil;
    RecyclerView rv_tip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activtiy_plantinfo);

        initRecyclerView();

        btn_info_close = findViewById(R.id.btn_info_close);
        btn_info_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String  plant_kind = intent.getStringExtra("plant_kind");
        txt_plantinfo_kind = findViewById(R.id.txt_plantinfo_kind);
        txt_plantinfo_kind.setText(plant_kind);

        String  soil_kind = intent.getStringExtra("soil_kind");
        txt_plantinfo_soil = findViewById(R.id.txt_plantinfo_soil);
        txt_plantinfo_soil.setText(soil_kind);
    }
    //tip 내용 초기화하는 메소드
    private void initRecyclerView(){
        rv_tip = findViewById(R.id.rv_tip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rv_tip.setLayoutManager(layoutManager);

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
        TipAdapter adapter = new TipAdapter();
        adapter.addItem(new ItemTip("물은 적당히!",R.drawable.ic_opacity_24px,
                "화초가 물에 잠기지 않도록 물을 줘야 해요\n화분의 흙을 젖어있는 상태가 아닌 촉촉한 정도로만 유지해도 됩니다",
                Color.parseColor("#FF6989B2")));
        adapter.addItem(new ItemTip("화분의 위치도 중요!",R.drawable.ic_wb_sunny_24px,
                "집안에 비치는 방향을 파악해서 햇볕이 드는 곳, 그늘이 지는 곳, 습도와 온도에 맞춰서 화분을 놓으세요",
                Color.parseColor("#FFF29661")));
        adapter.addItem(new ItemTip("생장에 알맞은 환경이 필요",R.drawable.ic_local_florist_24px,
                "분갈이 시 식물에 맞지 않는 큰 화분에 옮기지 마세요.\n지금 화분의 한 사이즈 큰 것이면 됩니다.",
                Color.parseColor("#FF508221")));
        rv_tip.setAdapter(adapter);
    }
}
