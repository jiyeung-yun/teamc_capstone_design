package com.capstone.plantplant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.HalfFloat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 3333;

    //저장소를 통해 사진을 입력할 요청코드
    private static final int REQUEST_SELECT_PICTURE = 501;
    //카메라를 통해 사진을 입력할 요청코드
    private static final int REQUEST_CODE_TAKE_PICTURE = 502;

    private static final String FILEPROVIDER_AUTHORITY ="com.capstone.plantplant.fileprovider";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageformat = new SimpleDateFormat("yyyyMMdd_HHmmSS");
    String filename;
    Intent request;

    ImageButton btn_camera,btn_gallery,btn_storage_cancel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상단 작업표시줄 투명하게 만드는 코드*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        /*상단 작업표시줄 투명하게 만드는 코드*/

        setContentView(R.layout.activity_storage);

        //카메라 권한 체크 및 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);

        //카메라를 통한 입력 버튼
        btn_camera = findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageCamara();
            }
        });

        //저장소를 통한 입력버튼
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageStorage();
            }
        });

        //취소 버튼
        btn_storage_cancel = findViewById(R.id.btn_storage_cancel);
        btn_storage_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        request = getIntent();

        filename = imageformat.format(new Date()) + ".png";
    }
    //카메라를 통해 사진을 입력할 경우
    public void getImageCamara() {
        File image = new File(Environment.getExternalStorageDirectory(), "/DCIM/PLANT/" + filename);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(getBaseContext(),FILEPROVIDER_AUTHORITY, image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }
    }
    //저장소를 통해 사진을 입력할 경우
    public void getImageStorage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_TAKE_PICTURE||requestCode == REQUEST_SELECT_PICTURE )
                if (data.getData() != null) {
                    try{
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        String path = saveToInternalStorage(img);
                        Log.d("Storage Activity","file path is "+path);

                        Intent intent = getIntent();
                        intent.putExtra("path",path);
                        intent.putExtra("filename",filename);
                        setResult(RESULT_OK,intent);
                        finish();
                        return;
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
        }
        setResult(RESULT_CANCELED);
        finish();
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        File mypath =new File(directory,filename);
        Log.d("Storage Activity","file makes : "+filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Log.d("Storage Activity","file Close");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 권한이 거절된 상태
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"식물 사진을 등록하기 위해서는 권한허용이 필요합니다.",Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
