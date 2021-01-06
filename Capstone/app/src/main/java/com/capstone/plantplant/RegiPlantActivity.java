package com.capstone.plantplant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.capstone.plantplant.model.Plant;
import com.capstone.plantplant.util.BluetoothLeService;
import com.capstone.plantplant.util.SampleGattAttributes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.capstone.plantplant.ListActivity.LIST_URI;
import static com.capstone.plantplant.ListActivity.plantList;

public class RegiPlantActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_IMAGE = 1011;
    private static final int REQUEST_PLANT_KIND = 1012;
    Toolbar toolbar_regiplant;

    TextView txt_kindplant,txt_lastwaterdate;
    DatePickerDialog calender;

    ImageView reg_plant_image;
    ImageButton btn_regi_img;

    Spinner spinner_soil;

    ToggleButton btn_connect;
    Button btn_regi;

    Plant plant;
    String plant_img, path; //사진 파일 경로


    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";              //넘겨 받은거
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private boolean isPermission = false;

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
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiplant);

        callPermission();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        if(mDeviceName != null && mDeviceAddress != null){
            Log.d("디바이스 이름 : ", mDeviceName);
            Log.d("디바이스  addr  : ", mDeviceAddress);
        }

        /*
        openBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if (sendmsg.equals("3")) {
                    sendmsg = "2";  //정지
                    makeChange(sendmsg);
                    openBtn.setText("OPEN");
                } else {
                    sendmsg = "3";  //열림
                    makeChange(sendmsg);
                    openBtn.setText("STOP");
                    closeBtn.setText("CLOSE");
                }
                //오픈 버튼 클릭시
            }
        });
         */


        toolbar_regiplant = findViewById(R.id.toolbar_regiplant);
        setSupportActionBar(toolbar_regiplant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));

        //식물의 종류를 검색하는 화면 버튼
        txt_kindplant = findViewById(R.id.txt_kindplant);
        txt_kindplant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kind  = new Intent(getApplicationContext(),SearchPlantActivity.class);
                startActivityForResult(kind,REQUEST_PLANT_KIND);
            }
        });

        //식물의 사진을 입력받는 버튼
        reg_plant_image = findViewById(R.id.reg_plant_image);
        btn_regi_img = findViewById(R.id.btn_regi_img);
        btn_regi_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent img = new Intent(getApplicationContext(),StorageActivity.class);
                startActivityForResult(img,REQUEST_IMAGE);
            }
        });

        //마지막으로 급수한 날짜 입력 관련
        txt_lastwaterdate = findViewById(R.id.txt_lastwaterdate);
        txt_lastwaterdate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        //오늘 날짜로 초기화
        int today_year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int today_month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int today_day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        calender = new DatePickerDialog(this ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s1 = year+"-"+(month+1)+"-"+dayOfMonth;
                String mod_date = "";
                try {
                    Date temp = new SimpleDateFormat("yyyy-M-d").parse(s1);
                    mod_date = new SimpleDateFormat("yyyy-MM-dd").format(temp);
                } catch (ParseException e) {e.printStackTrace();
                    mod_date = s1;
                }
                txt_lastwaterdate.setText(mod_date);
            }

        },today_year,today_month-1,today_day);

        txt_lastwaterdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calender.show();
            }
        });


        //모듈 연결 확인 버튼
        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mBluetoothLeService.disconnect();
                Intent intent1 = new Intent(getApplicationContext(), DeviceScanActivity.class);
                startActivity(intent1);
                // finish();
            }
        });

        //식물 아이템 등록 버튼
        btn_regi = findViewById(R.id.btn_regi);
        btn_regi.setOnClickListener(this);
        checkConnectState(true);

    }
    //모듈과의 연결이 확인되면 등록버튼 활성화
    private void checkConnectState(boolean check) {
        //모듈 연결 코드 필요
        if (check) {
            btn_regi.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }else {
            btn_regi.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary3));
        }
        btn_regi.setClickable(check);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_IMAGE:{

                if(resultCode==RESULT_OK){

                    plant_img = data.getStringExtra("filename");
                    Log.d("RegiPlantActivity","filename is "+plant_img);

                    path = data.getStringExtra("path");
                    Log.d("RegiPlantActivity","file path is "+path);

                    try {
                        File file=new File(path, plant_img);
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        Log.d("RegiPlantActivity","filename load complete");

                        reg_plant_image.setImageBitmap(bitmap);
                        reg_plant_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"이미지 로드를 실패하였습니다",Toast.LENGTH_SHORT).show();
                }
                break;
            }


            case REQUEST_PLANT_KIND:{

                if(resultCode==RESULT_OK){
                    plant = new Plant();

                    plant.setCntntsNo(data.getStringExtra("result_kind_num"));
                    plant.setCntntsSj(data.getStringExtra("result_kind_name"));

                    txt_kindplant.setText(plant.getCntntsSj());
                }
                break;
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;
        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessCoarseLocation = true;
        }
        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
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
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };
    private void displayData(String data) {         //데이터 받아와서 텍스트 뷰에 뿌려줌
        if (data != null) {

            String AT[] = data.split("\n");
            Log.d("받는 값 1: ",AT[0]);
            Log.d("받는 값 2: ",AT[1]);

        }
    }
    public boolean Check_ble_state() {
        boolean state = mConnected;
        state = !state;
        Log.d("체크 함수 커넥 상태", "Connect request result=" + state);
        return state;
    }

    public void initial_btn() {

    }

    public void check_Connect_Device() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RegiPlantActivity.this);
        alertDialogBuilder.setTitle("모듈 연결확인 불가");
        alertDialogBuilder.setMessage("연결 터치시 연결 페이지 이동").setCancelable(false).setPositiveButton("연결",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mBluetoothLeService.disconnect();
                        Intent intent1 = new Intent(getApplicationContext(), DeviceScanActivity.class);
                        startActivity(intent1);
                    }
                })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                                //    autos_witch.setChecked(false);
                                //     openBtn.setEnabled(true);
                                //     closeBtn.setEnabled(true);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
        }
    }
    private void makeChange(String msg) {
        String str = msg;
        // Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if (mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
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


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_regi){

            final String plant_kind = txt_kindplant.getText().toString();
            if(plant_kind.length()<1){
                Toast.makeText(getApplicationContext(),"식물 종류를 입력해주세요!",Toast.LENGTH_SHORT).show();
                return;
            }

            //토양의 종류
            spinner_soil = findViewById(R.id.spinner_soil);
            final int soil_kind = spinner_soil.getSelectedItemPosition();

            //등록버튼 클릭 당시 날짜를 받아서 저장함
            final String reg_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            Uri uri = new Uri.Builder().build().parse(LIST_URI);
            if(uri!=null) {
                ContentValues values = new ContentValues();
                values.put("kind", plant.getCntntsSj());
                values.put("date", reg_date);
                values.put("soil", soil_kind);
                int num = Integer.parseInt(plant.getCntntsNo());
                values.put("num", num);
                String last_date = txt_lastwaterdate.getText().toString();
                values.put("lastdate", last_date);
                //values.put("humidity", humid);
                //values.put("period", period);

                if(plant_img!=null && path!=null){
                    values.put("image", plant_img);
                    values.put("path", path);
                }

                uri = getContentResolver().insert(uri,values);
                Log.d("데이터베이스;식물리스트",  "INSERT 결과 =>"+uri);
            }

            setResult(RESULT_OK);
            Toast.makeText(getApplicationContext(),"식물이 정상적으로 등록되었습니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
