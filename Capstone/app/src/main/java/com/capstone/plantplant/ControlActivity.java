package com.capstone.plantplant;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.capstone.plantplant.util.BluetoothLeService;
import com.capstone.plantplant.util.SampleGattAttributes;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.capstone.plantplant.ListActivity.LIST_URI;
import static com.capstone.plantplant.ListActivity.plantList;


public class ControlActivity extends AppCompatActivity {
    Toolbar toolbar_control;

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


    AlertDialog.Builder dialog;

    Button btn_control_save,btn_reset_data;
    Spinner cont_spinner_time;

    SwitchMaterial switch_module_onoff,switch_module_water;
    LinearLayout ly_control_water;

    EditText editText_watertime,editText_waterdate,editText_waterhumidity;
    String watertime="",waterdate="",waterhumidity="";

    Spinner spinner_control_soil,spinner_control_pot;
    int index;

    Uri uri = new Uri.Builder().build().parse(LIST_URI);
    String[] colums = {"kind","soil"};

    String kind;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        /*액션바 => 툴바로 적용*/
        toolbar_control = findViewById(R.id.toolbar_control);
        setSupportActionBar(toolbar_control);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*액션바 => 툴바로 적용*/

        final Intent intent1 = getIntent();
        mDeviceName = intent1.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent1.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

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
                if(clearDeviceData(getIntent().getIntExtra("index",0))){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        //데이터를 삭제할 경우 보여지는 알림창 생성
        dialog.create();


        //모듈 전원 스위치
        switch_module_onoff = findViewById(R.id.switch_module_onoff);
        switch_module_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch_module_water.setChecked(isChecked);
            }
        });
    //    switch_module_onoff.setChecked(true);

        //급수 제어 정보 레이아웃
        ly_control_water = findViewById(R.id.ly_control_water);


        //자동 급수 스위치
        switch_module_water = findViewById(R.id.switch_module_water);
        switch_module_water.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && switch_module_onoff.isChecked() ){
                    ly_control_water.setVisibility(View.VISIBLE);
                }else {
                    if(!switch_module_onoff.isChecked()){
                        Toast.makeText(getApplicationContext(),"모듈의 전원이 켜져있을 때만\n자동 급수가 가능합니다",Toast.LENGTH_SHORT).show();
                        switch_module_water.setChecked(false);
                    }
                    ly_control_water.setVisibility(View.GONE);
                }
            }
        });


        //데이터 삭제 버튼
        btn_reset_data = findViewById(R.id.btn_reset_data);
        btn_reset_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        //오전/오후 스피너 초기화
        cont_spinner_time = (Spinner)findViewById(R.id.control_spinner);
        String[] days = {"오전","오후"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, days);
        cont_spinner_time.setAdapter(adapter);


        editText_watertime = findViewById(R.id.editText_watertime);
        editText_watertime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);
                    //입력값이 0~12가 되도록
                    if (integer > 12 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "시간 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_watertime.setText(watertime);
                    }else {
                        watertime = Integer.toString(integer);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });


        editText_waterdate = findViewById(R.id.editText_waterdate);
        editText_waterdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);

                    //입력값이 0~365범위가 되도록
                    if (integer > 365 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "기간 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_waterdate.setText(waterdate);
                    }else {
                        waterdate = Integer.toString(integer);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        editText_waterhumidity = findViewById(R.id.editText_waterhumidity);
        for(int i =0 ;i< plantList.size();i++){
           if(plantList.get(i).getPname().equals(kind)) {
               int humid = plantList.get(i).getPwater();
               editText_waterhumidity.setText(Integer.toString(humid));
               break;
           }
        }


        editText_waterhumidity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(text.length()>0){
                    int integer = Integer.parseInt(text);
                    //입력값이 0~100이 되도록
                    if (integer > 100 || integer < 0) {
                        Toast.makeText(getApplicationContext(), "기준 습도 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        editText_waterhumidity.setText(waterhumidity);
                    }else {
                        waterhumidity =  Integer.toString(integer);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        //DB에서 아이템 식물 정보를 불러옴
        Cursor cursor = getContentResolver().query(uri,colums,"_index="+index,null,null);
        while(cursor.moveToNext()) {
            //식물 종류
            kind = cursor.getString(cursor.getColumnIndex(colums[0]));

            //토양 종류
            spinner_control_soil = findViewById(R.id.spinner_control_soil);
            int soil_kind_pos = cursor.getInt(cursor.getColumnIndex(colums[1]));
            spinner_control_soil.setSelection(soil_kind_pos);


            int humidity = cursor.getInt(cursor.getColumnIndex(colums[2]));
            editText_waterhumidity.setText(Integer.toString(humidity));

            int time = cursor.getInt(cursor.getColumnIndex(colums[3]));
            if(time>12){
                cont_spinner_time.setSelection(1);
                editText_watertime.setText(Integer.toString(time-12));
            }else{
                cont_spinner_time.setSelection(0);
                editText_watertime.setText(Integer.toString(time));
            }

            int date = cursor.getInt(cursor.getColumnIndex(colums[4]));
            editText_waterdate.setText(Integer.toString(date));
        }

        //입력한 정보 저장
        btn_control_save = findViewById(R.id.btn_control_save);
        btn_control_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //입력한 정보를 기기 내 DB에 업데이트 후 모듈에 전달
                ContentValues values = new ContentValues();

                //습도
                int humidy = Integer.parseInt(editText_waterhumidity.getText().toString());
                values.put("humidity", humidy);

                //기간
                int date = Integer.parseInt(editText_waterdate.getText().toString());
                values.put("period", date);

                //시간
                int time = Integer.parseInt(editText_watertime.getText().toString());
                values.put("time", time);

                //토양
                values.put("soil", spinner_control_soil.getSelectedItemPosition());
                //values.put("size", spinner_control_pot.getSelectedItemPosition());


                int count = getContentResolver().update(uri,values,"_index="+index,null);
                Log.d("데이터베이스;식물리스트",  "UPDATE 결과 =>"+count+"개의 컬럼이 변경되었습니다.");

                Toast.makeText(getApplicationContext(),"성공적으로 설정하였습니다.",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);

                final Intent intent = new Intent(getApplicationContext(), RegiPlantActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //식물 아이템 삭제 메소드
    private boolean clearDeviceData(int index){
        //모듈과의 연결을 끊어야함
        int count = getContentResolver().delete(uri,"_index="+index,null);
        Log.d("데이터베이스;식물리스트",  "DELETE 결과 =>"+count+"개의 컬럼이 삭제되었습니다.");
        if(count>0){
            return true;
        }
        return false;
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


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

   /* @Override
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
    }*/

    /*@Override
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
    }*/

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

}
