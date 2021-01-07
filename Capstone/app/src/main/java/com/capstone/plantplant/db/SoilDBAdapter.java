package com.capstone.plantplant.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.capstone.plantplant.model.Soil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoilDBAdapter {

    // TODO : TABLE 이름을 명시해야함
    private static final String TABLE_NAME = "Soil";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private SoilDatabaseHelper mDbHelper;

    public SoilDBAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new SoilDatabaseHelper(mContext);
    }

    public SoilDBAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e("SoilDBAdapter", mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public SoilDBAdapter open() throws SQLException {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e("SoilDBAdapter", "open >>"+ mSQLException.toString());
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
            Soil soil = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // 칼럼의 마지막까지
                while( mCur.moveToNext() ) {

                    // TODO : 커스텀 모델 생성
                    soil = new Soil();

                    // TODO : Record 기술
                    // id, name, account, privateKey, secretKey, Comment
                    soil.setSID(mCur.getInt(0));
                    soil.setSname(mCur.getString(1));
                    soil.setStype(mCur.getString(2));
                    soil.setSproduce(mCur.getString(3));
                    soil.setSusage(mCur.getString(4));
                    soil.setScharacter(mCur.getString(5));

                    // 리스트에 넣기
                    userList.add(soil);
                }

            }
            return userList;
        }
        catch (SQLException mSQLException) {
            Log.e("SoilDBAdapter", "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}
