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
    public static final String DATABASE_NAME = "list.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "PLANTLIST";
    public static final String PLANT_ID = "_index";
    public static final String PLANT_KIND= "kind"; //식물의 종류
    public static final String PLANT_DATE= "date"; //등록 날짜
    public static final String PLANT_SOIL = "soil"; //토양 종류
    public static final String PLANT_LAST_DATE = "lastdate"; //마지막으로 급수한 날짜
    public static final String PLANT_NUM = "num"; //식물의 컨텐츠 번호
    public static final String PLANT_IMAGE = "image"; //식물 사진 파일명
    public static final String PLANT_IMAGE_PATH = "path"; //식물 사진 저장 경로

    public static final String[] ALL_COLUMS ={PLANT_ID,PLANT_KIND,PLANT_DATE,PLANT_SOIL,PLANT_LAST_DATE,PLANT_NUM,PLANT_IMAGE,PLANT_IMAGE_PATH};
    private static final String CREATE_TABLE ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME
            +"("+PLANT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +PLANT_KIND+" TEXT,"
            +PLANT_DATE+" TEXT,"
            +PLANT_SOIL+" INTEGER,"
            +PLANT_LAST_DATE+" TEXT,"
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

}
