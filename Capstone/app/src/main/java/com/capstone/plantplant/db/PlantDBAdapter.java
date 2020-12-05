package com.capstone.plantplant.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.capstone.plantplant.model.Plant;
import com.capstone.plantplant.model.Soil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlantDBAdapter {

    // TODO : TABLE 이름을 명시해야함
    private static final String TABLE_NAME = "Plant";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private PlantDatabaseHelpter mDbHelper;

    public PlantDBAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new PlantDatabaseHelpter(mContext);
    }

    public PlantDBAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e("PlantDBAdapter", mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public PlantDBAdapter open() throws SQLException {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e("PlantDBAdapter", "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public List getTableData() {
        try
        {
            // Table 이름 -> antpool_bitcoin 불러오기
            String sql ="SELECT * FROM " + TABLE_NAME;

            // 모델 넣을 리스트 생성
            List userList = new ArrayList();

            // TODO : 모델 선언
            Plant plant = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // 칼럼의 마지막까지
                while( mCur.moveToNext() ) {

                    // TODO : 커스텀 모델 생성
                    plant = new Plant();

                    // TODO : Record 기술
                    // id, name, account, privateKey, secretKey, Comment
                    plant.setPID(mCur.getInt(0));
                    plant.setPname(mCur.getString(1));
                    plant.setPexp(mCur.getString(2));
                    plant.setPwater(mCur.getInt(3));
                    plant.setPtime(mCur.getInt(4));

                    // 리스트에 넣기
                    userList.add(plant);
                }

            }
            return userList;
        }
        catch (SQLException mSQLException) {
            Log.e("PlantDBAdapter", "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}
