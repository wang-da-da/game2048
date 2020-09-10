package com.wdd.new2048;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_RANK = "create table Rank ("
                                             + "id integer primary key , "
                                             + "username text, "
                                             + "scores integer, "
                                             + "diff integer)";
    private Context mContext;
    public MyDatabaseHelper( Context context,  String name,
                             SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_RANK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists Rank");
            onCreate(db);
    }
}
