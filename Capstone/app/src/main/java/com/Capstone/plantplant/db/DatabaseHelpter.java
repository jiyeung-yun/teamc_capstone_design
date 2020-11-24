package com.capstone.plantplant.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseHelpter extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "plant.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "PLANTLIST";
    public static final String PLANT_ID = "_index";
    public static final String PLANT_KIND= "kind"; //식물의 종류
    public static final String PLANT_DATE= "date"; //등록 날짜
    public static final String PLANT_SOIL = "soil"; //토양 종류
    public static final String PLANT_SIZE = "size"; //화분 사이즈
    public static final String PLANT_NUM = "num"; //식물의 도감 번호
    public static final String PLANT_IMAGE = "image"; //식물 사진 파일명
    public static final String PLANT_IMAGE_PATH = "path"; //식물 사진 저장 경로

    public static final String[] ALL_COLUMS ={PLANT_ID,PLANT_KIND,PLANT_DATE,PLANT_SOIL,PLANT_SIZE,PLANT_NUM,PLANT_IMAGE,PLANT_IMAGE_PATH};
    private static final String CREATE_TABLE ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME
            +"("+PLANT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +PLANT_KIND+" TEXT,"
            +PLANT_DATE+" TEXT,"
            +PLANT_SOIL+" INTEGER,"
            +PLANT_SIZE+" INTEGER,"
            +PLANT_NUM+" INTEGER,"
            +PLANT_IMAGE+" TEXT,"
            +PLANT_IMAGE_PATH+" TEXT"
            + ")";

    public DatabaseHelpter(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //식물 리스트 테이블 생성
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    final String sdb_name = "soil.db";

    //데이터베이스 생성여부 확인
    public boolean isCheckDB(Context context){
        String filePath = "/data/data/" + context.getPackageName() + "/databases/" + sdb_name;
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    // DB를 복사하기
    // assets의 /db/xxxx.db 파일을 설치된 프로그램의 내부 DB공간으로 복사하기
    public void copyDB(Context context){
        Log.d("SplashActivity", "soil.db => copy start");
        AssetManager manager = context.getAssets();
        String folderPath = "/data/data/" + context.getPackageName() + "/databases";
        String filePath = "/data/data/" + context.getPackageName() + "/databases/" + sdb_name;
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("db/" + sdb_name);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (folder.exists()) {

            }else{ folder.mkdirs(); }
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("SplashActivity","ErrorMessage : "+ e.getMessage());
        }
    }
}
