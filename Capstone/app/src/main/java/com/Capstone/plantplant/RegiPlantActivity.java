package com.capstone.plantplant;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.capstone.plantplant.ListActivity.LIST_URI;

public class RegiPlantActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 1011;
    private static final int REQUEST_PLANT_KIND = 1012;

    Toolbar toolbar_regiplant;

    TextView txt_kindplant;
    ImageView reg_plant_image;

    Spinner spinner_pot,spinner_soil;

    ToggleButton btn_connect;
    Button btn_regi;

    int plant_idx;
    String plant_img; //사진 파일 이름
    String path; //사진 파일 경로

    private static final int REQUEST_ENABLE_BT = 1;    // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter;          // 블루투스 어댑터
    private Set<BluetoothDevice> devices;               // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice;            // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null;     // 블루투스 소켓
    private OutputStream outputStream = null;           // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null;             // 블루투스에 데이터를 입력하기 위한 입력 스트림
    // private Thread bluetoothThread = null;                 // 문자열 수신에 사용되는 쓰레드
    private byte[] sendByte = new byte[4];            // 수신된 문자열을 저장하기 위한 버퍼
    public ProgressDialog progressDialog;
    public boolean onBT = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiplant);

        /*상단 툴바 기본 설정 초기화*/
        toolbar_regiplant = findViewById(R.id.toolbar_regiplant);
        setSupportActionBar(toolbar_regiplant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_keyboard_backspace_24px));
        /*상단 툴바 기본 설정 초기화*/

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

        //모듈 연결 확인 버튼
        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){      // 연결
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(bluetoothAdapter == null) {  // 장치가 블루투스 지원하지 않는 경우
                        Toast.makeText(getApplicationContext(), "Bluetooth 지원을 하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
                    } else {    // 블루투스 지원하는 경우
                        if (!bluetoothAdapter.isEnabled()) {
                            // 블루투스를 지원하지만 비활성 상태인 경우
                            // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
                            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
                        } else {
                            // 블루투스를 지원하며 활성 상태인 경우
                            // 페어링된 기기 목록을 보여주고 연결할 장치 선택
                            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                            if (pairedDevices.size() > 0) { // 페어링된 장치 있는 경우
                                Toast.makeText(getApplicationContext(), "모듈과 연결합니다.", Toast.LENGTH_SHORT).show();
                                selectDevice();
                            } else {  // 페어링된 장치 없는 경우
                                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{      // 연결안함
                    try {
                        BTSend.interrupt();  // 데이터 송신 쓰레드 종료
                        inputStream.close();
                        outputStream.close();
                        bluetoothSocket.close();
                        b = false;
                        Toast.makeText(getApplicationContext(),"모듈과 연결을 끊습니다.",Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored){

                    }
                }
                checkConnectState(b);       // 이 함수에 짤 코드를 여기에 다 넣어서 이 함수 없어도 될 것 같은데 나중에 확인 한 번 해주세용
            }
        });

        //식물 아이템 등록 버튼
        btn_regi = findViewById(R.id.btn_regi);
        btn_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmp = txt_kindplant.getText().toString();
                final String plant_kind = tmp.substring(0,tmp.length()-1);
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

                //화분의 사이즈
                spinner_pot = findViewById(R.id.spinner_pot);
                final int pot_size = spinner_pot.getSelectedItemPosition();

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
                            values.put("size", pot_size);
                            values.put("num", plant_idx);


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
                },1000);

            }
        });
        checkConnectState(btn_connect.isChecked());
    }
    final String ServiceKey = "Xzd9L81I4P%2F%2FI6OaxEbY9FmvA5KUOJDEsk82pe396jZY0MfLk0IQn1BYbpv1JYnxu4kZ7pRf38PjCqsaOd2DwQ%3D%3D"; //인증키
    //한 페이지에 아이템 갯수
    int numOfRows = 1;
    //로드할 페이지 번호
    int pageNo = 1;
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
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + numOfRows); // 한 페이지 결과 수
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

    //모듈과의 연결이 확인되면 등록버튼 활성화
    private void checkConnectState(boolean check){
        //모듈 연결 코드 필요
        if(check){
            if(bluetoothAdapter.isEnabled()) {
            }
            btn_regi.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));

        }else{
            btn_regi.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary3));
        }
        btn_regi.setClickable(check);
    }

    // 블루투스 장치 이름을 리스트로 작성해서 AlertDialog 띄움
    public void selectDevice() {
        devices = bluetoothAdapter.getBondedDevices();
        final int pairedDeviceCount = devices.size();

        if(pairedDeviceCount == 0) {
            // 페어링 된 장치가 없는 경우
            Toast.makeText(getApplicationContext(), "장치를 페어링 해주세요", Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 페어링 된 블루투스 장치 이름 목록 작성
        List<String> listItems = new ArrayList<>();
        for(BluetoothDevice device : devices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");     // 취소 항목 추가

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if(item == pairedDeviceCount) {
                    // 연결할 장치를 선택하지 않고 취소 누른 경우
                    finish();
                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });

        builder.setCancelable(false);   // 뒤로 가기 버튼 사용 금지 ... 왜 ? 나중에 실행 때 직접 확인하기
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 블루투스 연결
    public void connectToSelectedDevice(final String selectedDeviceName){
        bluetoothDevice = getDeviceFromBondedList(selectedDeviceName);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("블루투스 연결중 ...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        Thread BTConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");  // 모듈 UUID 입력하는 곳 현재는 예시로 넣어놨습니다.(HC-06 UUID)
                    // 소켓 생성
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);

                    // RFCOMM 채널을 통한 연결
                    bluetoothSocket.connect();

                    // 데이터 송수신을 위한 스트림 열기
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),selectedDeviceName + " 연결 완료", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });

                    btn_connect.setChecked(true);

                } catch (Exception e) {
                    // 블루투스 연결 중 오류 발생
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"블루투스 연결 오류",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        BTConnect.start();
    }

    public BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : devices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    // 블루투스 데이터 송신
    Thread BTSend = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                outputStream.write(sendByte);
            }catch (Exception e) {
                // 문자열 전송 도중 오류가 발생한 경우
            }
        }
    });

    // 4byte 프로토콜의 데이터 송신이 가능
    // 프로토콜을 아두이노에 송신하는 메소드
    public void sendByteData(int btLightPercent) throws IOException {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0xa5;
        bytes[1] = (byte) 0x5a;
        bytes[2] = 1;   // command
        bytes[3] = (byte) btLightPercent;
        sendByte = bytes;
        BTSend.run();
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
}
