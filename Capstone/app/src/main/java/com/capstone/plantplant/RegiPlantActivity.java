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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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

    Spinner spinner_pot,spinner_soil;

    ToggleButton btn_connect;
    Button btn_regi;

    int plant_idx;
    String plant_img; //사진 파일 이름
    String path; //사진 파일 경로


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
        reg_plant_image.setOnClickListener(new View.OnClickListener() {
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
                    String  result_kind = data.getStringExtra("result_kind");
                    txt_kindplant.setText(result_kind);
                }
                break;
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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



    final String ServiceKey = "Xzd9L81I4P%2F%2FI6OaxEbY9FmvA5KUOJDEsk82pe396jZY0MfLk0IQn1BYbpv1JYnxu4kZ7pRf38PjCqsaOd2DwQ%3D%3D"; //인증키
    //태그 확인
    boolean familyKorNm = false;
    boolean plantPilbkNo = false;
    void getEncyclopediaNum(String str){
        try {
            Log.d("RegiPlantActivity","검색어  => "+str);

            StringBuilder urlBuilder = new StringBuilder("http://openapi.nature.go.kr/openapi/service/rest/PlantService/plntIlstrSearch");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ServiceKey); //공공데이터포털에서 받은 인증키
            urlBuilder.append("&" + URLEncoder.encode("st","UTF-8") + "=" + 1); //검색어 구분 (st = 3 : 국문명일치)
            urlBuilder.append("&" + URLEncoder.encode("sw","UTF-8") + "=" + URLEncoder.encode(str, "UTF-8")); // 검색어
            int numOfRows = 1;
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + numOfRows); // 한 페이지 결과 수

            int pageNo = 1;
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + pageNo); //페이지 번호

            URL url = new URL(urlBuilder.toString());
            Log.d("RegiPlantActivity","URI 주소 => "+url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");


            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            try{
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(rd);

                int eventType = xmlPullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT){


                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:{
                            Log.d("RegiPlantActivity","API 파싱 => 시작");

                            break;
                        }
                        case XmlPullParser.START_TAG:{
                            String string = xmlPullParser.getName();
                            if(string.equals("item")){
                                Log.d("RegiPlantActivity","<item>");
                            }
                            if(string.equals("familyKorNm")){
                                familyKorNm = true;
                            }
                            if(string.equals("plantPilbkNo")){
                                plantPilbkNo = true;
                            }
                            break;
                        }
                        case XmlPullParser.TEXT:{
                            if(familyKorNm){
                                String s = xmlPullParser.getText();
                                Log.d("RegiPlantActivity","국문명 => "+s);
                                familyKorNm = false;
                            }
                            if(plantPilbkNo){
                                String s = xmlPullParser.getText();
                                Log.d("RegiPlantActivity","도감 번호 => "+s);

                                plant_idx= Integer.parseInt(s);
                                plantPilbkNo = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            break;
                        }
                    }


                    eventType = xmlPullParser.next();


                }
            }catch (XmlPullParserException e){
                Log.d("RegiPlantActivity","API 파싱 실패=> "+ e.getMessage());
            }


            rd.close();
            conn.disconnect();
            Log.d("RegiPlantActivity","API 파싱 => 끝");


        } catch (Exception e) {
            Log.d("RegiPlantActivity","API 파싱 실패=> "+ e.getMessage());
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_regi){

            final String plant_kind = txt_kindplant.getText().toString();
            if(plant_kind.length()<1){
                Toast.makeText(getApplicationContext(),"식물 종류를 입력해주세요!",Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    getEncyclopediaNum(plant_kind);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                        }
                    });

                }
            }).start();

            //토양의 종류
            spinner_soil = findViewById(R.id.spinner_soil);
            final int soil_kind = spinner_soil.getSelectedItemPosition();

                /*
                //화분의 사이즈
                spinner_pot = findViewById(R.id.spinner_pot);
                final int pot_size = spinner_pot.getSelectedItemPosition();
                */


            //등록버튼 클릭 당시 날짜를 받아서 저장함
            final String reg_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Uri uri = new Uri.Builder().build().parse(LIST_URI);
                    if(uri!=null) {
                        ContentValues values = new ContentValues();
                        values.put("kind", plant_kind);
                        values.put("date", reg_date);
                        values.put("soil", soil_kind);
                        //values.put("size", pot_size);
                        values.put("num", plant_idx);
                        String last_date = txt_lastwaterdate.getText().toString();
                        values.put("lastdate", last_date);

                        for(int i =0 ;i< plantList.size();i++){
                            if(plantList.get(i).getPname().equals(plant_kind)) {
                                int humid = plantList.get(i).getPwater();
                                values.put("humidity", humid);
                                int period = plantList.get(i).getPtime();
                                values.put("period", period);
                                break;
                            }
                        }


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
            },800);

        }
    }
}