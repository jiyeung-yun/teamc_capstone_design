package com.capstone.plantplant;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBProvider extends ContentProvider {
    private static final String AUTHORITY ="com.capstone.plantplant";
    private static final String LIST_PATH = "list";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+LIST_PATH);

    private static final int LIST = 1;
    private static final int LIST_ID = 2;

    private static final String SOIL_PATH = "soil";
    public static final Uri SOIL_URI = Uri.parse("content://"+AUTHORITY+"/"+SOIL_PATH);

    private static final int SOIL = 3;
    private static final int SOIL_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,LIST_PATH,LIST);
        uriMatcher.addURI(AUTHORITY,LIST_PATH+"/#",LIST_ID);

        uriMatcher.addURI(AUTHORITY,SOIL_PATH,SOIL);
        uriMatcher.addURI(AUTHORITY,SOIL_PATH+"/#",SOIL_ID);
    }

    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        DatabaseHelpter helpter = new DatabaseHelpter(getContext());
        db = helpter.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)){
            case LIST:
                cursor = db.query(DatabaseHelpter.TABLE_NAME,DatabaseHelpter.ALL_COLUMS,selection,selectionArgs,null,null,null);
                break;
            case SOIL:
                cursor = db.query(DatabaseHelpter.TABLE_NAME2,DatabaseHelpter.ALL_COLUMS2,selection,selectionArgs,null,null,null);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 경로 : "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case LIST:
                return "vnd.android.cursor.dir/list";
            case SOIL:
                return "vnd.android.cursor.dir/soil";
            default:
                throw new IllegalArgumentException("알 수 없는 경로 : "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)){
            case LIST:
                long id = db.insert(DatabaseHelpter.TABLE_NAME,null,values);
                if(id>0){
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI,id);
                    getContext().getContentResolver().notifyChange(_uri,null);
                    return uri;
                }
                break;
            case SOIL:
                long id2 = db.insert(DatabaseHelpter.TABLE_NAME2,null,values);
                if(id2>0){
                    Uri _uri = ContentUris.withAppendedId(SOIL_URI,id2);
                    getContext().getContentResolver().notifyChange(_uri,null);
                    return uri;
                }
                break;
            default:
                throw new SQLException("INSERT 명령어 실패 => "+uri);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case LIST:
                count = db.delete(DatabaseHelpter.TABLE_NAME,selection,selectionArgs);
                break;
            case SOIL:
                count = db.delete(DatabaseHelpter.TABLE_NAME2,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 경로 : "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case LIST:
                count = db.update(DatabaseHelpter.TABLE_NAME,values,selection,selectionArgs);
                break;
            case SOIL:
                count = db.update(DatabaseHelpter.TABLE_NAME2,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 경로 : "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }
}
