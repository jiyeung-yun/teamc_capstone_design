package com.capstone.plantplant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_PICTURE = 501;
    private static final int REQUEST_CODE_TAKE_PICTURE = 502;

    private static final String FILEPROVIDER_AUTHORITY ="com.capstone.plantplant.fileprovider";
    private static String FILE_NAME = "/DCIM/Camera/";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageformat = new SimpleDateFormat("yyyyMMdd_HHmmSS");

    Intent request;

    ImageButton btn_camera,btn_gallery,btn_storage_cancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storage);

        btn_camera = findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_storage_cancel = findViewById(R.id.btn_storage_cancel);
        btn_storage_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        request = getIntent();
    }

    public void openCamera() {
        String workImageName = imageformat.format(new Date()) + ".png";
        File image = new File(Environment.getExternalStorageDirectory(), FILE_NAME + workImageName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(getBaseContext(),FILEPROVIDER_AUTHORITY, image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }
    }
    public void openGallery() {
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
                        //사진 등록 방식 결정 후 코드 설계
                        setResult(RESULT_OK);
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
}
