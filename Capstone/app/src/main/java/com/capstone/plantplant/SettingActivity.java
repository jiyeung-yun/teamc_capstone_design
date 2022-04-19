package com.capstone.plantplant;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.plantplant.model.PlantToServer;
import com.capstone.plantplant.network.API;
import com.capstone.plantplant.network.RetrofitService;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.capstone.plantplant.ListActivity.LIST_URI;
import static com.capstone.plantplant.db.ListDBHelper.ALL_COLUMS;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    AlertDialog.Builder dialog;

    ImageButton btn_setting_close;
    Button btn_reset_data,btn_direct_water;

    SwitchMaterial switch_module_water;
    LinearLayout ly_control_water;
    PlantToServer plant;

    Spinner spinner_control_soil;
    int index;

    Uri uri = new Uri.Builder().build().parse(LIST_URI);

    String kind;
    int standard_humidity,device_id;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        //자동 급수 스위치
        switch_module_water = findViewById(R.id.switch_module_water);
        btn_direct_water = findViewById(R.id.btn_direct_water);
        btn_direct_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plant.setWatering(true);
                API api_ = RetrofitService.getServer().create(API.class);
                Call<ResponseBody> call = api_.onDirectWater(plant);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){
                            Toast.makeText(getApplicationContext(),"화분에 물주기 명령을 전달했습니다.\n잠시 후 물 주기를 시작합니다.",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                            noChangeFinish();
                        }

                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        noChangeFinish();
                    }
                });
            }
        });
        btn_setting_close = findViewById(R.id.btn_setting_close);
        btn_setting_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
            }
        });

        Snackbar.make(switch_module_water, "서버에 등록된 설정을 불러오는 중입니다.", Snackbar.LENGTH_SHORT).show();

        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        //DB에서 아이템 식물 정보를 불러옴
        Cursor cursor = getContentResolver().query(uri,ALL_COLUMS,"_index="+index,null,null);
        while(cursor.moveToNext()) {

            //식물 종류
            kind = cursor.getString(cursor.getColumnIndex(ALL_COLUMS[1]));
            plant = new PlantToServer(kind,getApplicationContext());

            //서버등록 기기명
            device_id =  cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[8]));

            //토양 종류
            spinner_control_soil = findViewById(R.id.spinner_control_soil);
            int soil_kind_pos = cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[3]));
            spinner_control_soil.setSelection(soil_kind_pos);

            standard_humidity =  cursor.getInt(cursor.getColumnIndex(ALL_COLUMS[4]));
            API api_ = RetrofitService.getServer().create(API.class);
            Call<ResponseBody> call = api_.getPlantData(kind);
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
                                    Toast.makeText(getApplicationContext(),"서버의 데이터를 불러오던 중에\n오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
                                    return;
                                }
                                JSONObject item_body_ = new JSONObject(item_body);
                                String item_body2 = item_body_.getString("Item");
                                JSONObject item_data = new JSONObject(item_body2);

                                //기준 습도 값
                                int shumidity = item_data.getInt("standard_humidity");
                                if(standard_humidity==shumidity) {
                                    switch_module_water.setChecked(true);
                                }else{
                                    switch_module_water.setChecked(false);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"서버의 데이터를 불러오던 중에\n오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                                noChangeFinish();
                            }
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        noChangeFinish();
                    }

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    noChangeFinish();
                }
            });
        }


        switch_module_water.setOnClickListener(this);
        
        
        //데이터를 삭제할 경우 보여지는 알림창 초기화
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("데이터초기화");
        dialog.setMessage("정말로 삭제하시겠습니까?\n해당 식물 정보는 모두 삭제됩니다.");
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDeletePlant(getIntent().getIntExtra("index",0));
            }
        });

        //데이터를 삭제할 경우 보여지는 알림창 생성
        dialog.create();



        //데이터 삭제 버튼
        btn_reset_data = findViewById(R.id.btn_reset_data);
        btn_reset_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        spinner_control_soil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //입력한 정보를 기기 내 DB에 업데이트 후 모듈에 전달
                ContentValues values = new ContentValues();

                //토양
                values.put("soil", spinner_control_soil.getSelectedItemPosition());
                //values.put("size", spinner_control_pot.getSelectedItemPosition());

                int count = getContentResolver().update(uri,values,"_index="+index,null);
                Log.d("데이터베이스;식물리스트",  "UPDATE 결과 =>"+count+"개의 컬럼이 변경되었습니다.");

                //noChangeFinish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        boolean isChecked = switch_module_water.isChecked();
        PlantToServer plantToServer = new PlantToServer(kind,getApplicationContext());
        if(isChecked){
            plantToServer.setStandard_humidity(standard_humidity);
        }else {
            plantToServer.setStandard_humidity(0);
        }
        API api_ = RetrofitService.postServer().create(API.class);
        Call<ResponseBody> call = api_.onControlMoter(plantToServer);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()!=200){
                    switch_module_water.setChecked(!isChecked);
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                switch_module_water.setChecked(!isChecked);
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //식물 아이템 삭제 메소드
    private void onDeletePlant(int index){
        API api_ = RetrofitService.postServer().create(API.class);
        Call<ResponseBody> call = api_.onDeleteData(new PlantToServer(kind,0,device_id,getApplicationContext()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    int count = getContentResolver().delete(uri,"_index="+index,null);
                    Log.d("데이터베이스;식물리스트",  "DELETE 결과 =>"+count+"개의 컬럼이 삭제되었습니다.");
                    if(count>0){
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void noChangeFinish(){
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(getChangingConfigurations(),R.anim.slide_down);
    }

/*

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    public final static UUID HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";              //넘겨 받은거
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";        //넘겨 받은거

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                // Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(context, "DEVICE CONNECTED", Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
                Log.d("블투 커넥 상태", "Connect request result=" + mConnected);
                // mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(context, "DEVICE DISCONNECTED", Toast.LENGTH_LONG).show();
                Log.d("블투 커넥 상태", "Connect request result=" + mConnected);
                invalidateOptionsMenu();
                //  clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };


    private void makeChange(String msg) {
        String str = msg;

        final byte[] tx = str.getBytes();
        if (mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("커넥 상태", "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);

        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            //mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
*/
}
