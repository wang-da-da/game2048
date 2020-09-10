package com.wdd.new2048;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyProvider extends ContentProvider {
    public MyProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    /*
    * uri ： which tables to query
    * projection : which columns to query
    * selection and selectionArgs :  Constrain which rows to query
    * sortOrder : sort the  result*/

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //返回数据库工具类的查询方法
        if (projection == null && selection == null && selectionArgs == null && sortOrder == null) {
            return SqlTools.Convenient_Query();
        } else {
            SQLiteDatabase db = SqlTools.deHelper.getWritableDatabase();
            Cursor cursor = db.query("Rank", projection, selection, selectionArgs, null, null, sortOrder);
            return cursor;
        }
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
