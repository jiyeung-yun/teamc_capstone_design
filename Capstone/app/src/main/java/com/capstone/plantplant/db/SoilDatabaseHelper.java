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
import java.io.OutputStream;

public class SoilDatabaseHelper extends SQLiteOpenHelper {
    public static final String SOIL_DATABASE_NAME = "Soil.db";

    public final String DB_PATH;

    public Context hcontext;
    private SQLiteDatabase mDataBase;

    public SoilDatabaseHelper(Context context){
        super(context,SOIL_DATABASE_NAME,null,1);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        this.hcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    //데이터베이스 생성여부 확인
    public boolean checkDBExist(){
        File file = new File(DB_PATH + SOIL_DATABASE_NAME);
        return file.exists();
    }


    public void createDataBase() throws IOException {
        //데이터베이스가 없으면 asset폴더에서 복사해온다.
        if(!checkDBExist()) {
            this.getReadableDatabase();
            this.close();

            try {
                //Copy the database from assests
                copyDataBase();
                Log.e("SoilDatabaseHelpter", "createDatabase database created");
            }catch (IOException mIOException) {
                Log.e("SoilDatabaseHelpter", "Error Create DB =>"+mIOException.getMessage());
            }
        }
    }


    //assets폴더에서 데이터베이스를 복사한다.
    private void copyDataBase() throws IOException {
        AssetManager manager = hcontext.getAssets();
        String folderPath = hcontext.getApplicationInfo().dataDir + "/databases";
        String filePath = DB_PATH + SOIL_DATABASE_NAME;
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("db/" + SOIL_DATABASE_NAME);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1; byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("SoilDatabaseHelpter", "ErrorMessage : "+e.getMessage()); }

    }


    //데이터베이스를 열어서 쿼리를 쓸수있게만든다.
    public boolean openDataBase() {
        String mPath = DB_PATH + SOIL_DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }



    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }
}
