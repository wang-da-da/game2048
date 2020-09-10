package com.wdd.new2048;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class HelpActivity extends AppCompatActivity {


    private Button btBack5;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        PublicWay.activityList.add(this);

        //向应用中写入Game_Data文件
        sharedPreferences = getSharedPreferences("GameData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //音乐初始化
        soundPool = new SoundPool.Builder().setMaxStreams(soundResIds.length).build();
        soundIdMap.put(0, soundPool.load(this, soundResIds[0], 1));
        soundIdMap.put(1, soundPool.load(this, soundResIds[1], 1));
        soundIdMap.put(2, soundPool.load(this, soundResIds[2], 1));


        //Backing to the MenuActivity
        btBack5 = (Button) findViewById(R.id.btback5);
        btBack5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HelpActivity.this, MenuActivity.class);
                startActivity(intent);

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
            }
        });
        
    }

}
