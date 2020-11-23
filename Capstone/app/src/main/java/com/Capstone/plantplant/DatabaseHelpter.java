package com.capstone.plantplant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelpter extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "plant.db";
    private static final int DATABASE_VERSION = 1;

    /*-----------------------식물 리스트 시작-----------------------*/
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
    /*-----------------------식물 리스트 끝-----------------------*/


    /*-----------------------토양 리스트 시작-----------------------
    public static final String TABLE_NAME2 = "SOILLIST";
    public static final String SOIL_ID = "_index";
    public static final String SOIL_KIND= "kind";  //토양의 종류
    public static final String SOIL_PRODUCE= "produce"; //생성
    public static final String SOIL_USAGE = "usage"; // 용도
    public static final String SOIL_FEATURE = "feature"; //특징

    public static final String[] ALL_COLUMS2 ={SOIL_ID,SOIL_KIND,SOIL_PRODUCE,SOIL_USAGE,SOIL_FEATURE};
    private static final String CREATE_TABLE2 ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME2
            +"("+SOIL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +SOIL_KIND+" TEXT,"
            +SOIL_PRODUCE+" TEXT,"
            +SOIL_USAGE+" TEXT,"
            +SOIL_FEATURE+" TEXT)";

   -----------------------토양 리스트 끝-----------------------*/

    public DatabaseHelpter(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //식물 리스트 테이블 생성
        db.execSQL(CREATE_TABLE);

        //토양 정보 리스트 테이블 생성
        //db.execSQL(CREATE_TABLE2);

        //토양 정보 리스트 속성 값 추가해야함함
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        onCreate(db);
    }
}
