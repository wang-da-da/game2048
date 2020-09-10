package com.wdd.new2048;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class RankActivity extends AppCompatActivity {

    private ArrayList<Map<String, String>> listItems;

    private Button btBack3;

    public Vibrator vibrator;

    //用于读
    private SharedPreferences sharedPreferences;
    //用于写
    private SharedPreferences.Editor editor;

    //播放短小音频
    public SoundPool soundPool;
    public int[] soundResIds = {R.raw.ding, R.raw.baji, R.raw.shua};
    public HashMap<Integer, Integer> soundIdMap = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_rank);

        PublicWay.activityList.add(this);

        //向应用中写入Game_Data文件
        sharedPreferences = getSharedPreferences("GameData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //音乐初始化
        soundPool = new SoundPool.Builder().setMaxStreams(soundResIds.length).build();
        soundIdMap.put(0, soundPool.load(this, soundResIds[0], 1));
        soundIdMap.put(1, soundPool.load(this, soundResIds[1], 1));
        soundIdMap.put(2, soundPool.load(this, soundResIds[2], 1));

        //启动的时候连接数据库
        SqlTools.deHelper = new MyDatabaseHelper(this,"Rank.db",null,6);

        Cursor cursor = SqlTools.Convenient_Query();

        //getting the List of SQLite
        listItems = SqlTools.converCusorTolist(cursor);

        //create a SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.rank_item,
                new String[]{"id", "username", "scores", "diff"}, new int[]{R.id.item_id, R.id.item_name, R.id.item_score,R.id.item_diff});


        ListView list = findViewById(R.id.rank_list);
        //显示数据
        list.setAdapter(simpleAdapter);

        //Backing to the MenuActivity
        btBack3 = (Button) findViewById(R.id.btback3);
        btBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//                }

                //对播放的音乐进行判断
                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }

                Intent intent = new Intent();
                intent.setClass(RankActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

}