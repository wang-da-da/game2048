package com.wdd.new2048;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SqlTools {

    //数据库对象，在启动的时候被赋值
    public static MyDatabaseHelper deHelper;

    //插入方法，
    public static void Insert(String name, int score, int diff) {
        SQLiteDatabase db = deHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", name);
        values.put("scores", score);
        values.put("diff", diff);
        db.insert("Rank", null, values);
    }

    //查询方法
    //全集查询,用于测试
    public static Cursor Query() {
        SQLiteDatabase db = deHelper.getWritableDatabase();
        Cursor cursor = db.query("Rank", null, null, null, null, null, null);
        return cursor;
    }
//
    //查询方法
    //按照分数排名,不含id
    public static Cursor Convenient_Query() {
        SQLiteDatabase db = deHelper.getWritableDatabase();
        Cursor cursor = db.query("Rank", new String[]{"id","username",  "scores", "diff"}, null, null, null, null, "scores desc");
        return cursor;
    }

    //将游标转换成List，其中数据库每一列转换成一个Map
    public static ArrayList<Map<String, String>> converCusorTolist(Cursor cursor) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        //遍历Cursor结果集
        while (cursor.moveToNext()) {
            //将结果集中的数据存入ArrayList中
            Map<String, String> map = new HashMap<>();
            //取出查询记录中的值
            map.put("id",cursor.getInt(0) + "");
            map.put("username", cursor.getString(1));
            map.put("scores", cursor.getInt(2) + "");
            map.put("diff", cursor.getInt(3) + "");
            result.add(map);
        }
        cursor.close();
        return result;
    }

    //删除排行榜的所有数据
    public static void DeleteAll() {
        SQLiteDatabase db = deHelper.getWritableDatabase();
        db.delete("Rank", "id > ?", new String[]{"0"});
        Log.d("Delte", "delete succeed");
    }
}
