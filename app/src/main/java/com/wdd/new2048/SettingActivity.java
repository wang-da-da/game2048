package com.wdd.new2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private Button btCreate;
    private Button btBack4;
    private Button clearRank;
    public Vibrator vibrator;

    public SoundPool soundPool;//播放短小音频
    public int[] soundResIds = {R.raw.ding, R.raw.baji, R.raw.shua};
    public HashMap<Integer, Integer> soundIdMap = new HashMap<>();

    //用于读
    private SharedPreferences sharedPreferences;
    //用于写
    private SharedPreferences.Editor editor;
    public int lines;

    private static SettingActivity settingActivity = null;

    public static SettingActivity getSettingActivity() {

        return settingActivity;
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //向应用中写入GameData文件
        sharedPreferences = getSharedPreferences("GameData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        PublicWay.activityList.add(this);

        RadioGroup rgMusic = findViewById(R.id.rgmusic);
        RadioButton music1 = findViewById(R.id.music1);//Metu
        RadioButton music2 = findViewById(R.id.music2);//Ding
        RadioButton music3 = findViewById(R.id.music3);//Baji
        RadioButton music4 = findViewById(R.id.music4);//Shua

        soundPool = new SoundPool.Builder().setMaxStreams(soundResIds.length).build();
        soundIdMap.put(0, soundPool.load(this, soundResIds[0], 1));//Ding
        soundIdMap.put(1, soundPool.load(this, soundResIds[1], 1));//Baji
        soundIdMap.put(2, soundPool.load(this, soundResIds[2], 1));//Shua

        int music = sharedPreferences.getInt("music", 0);
        if (music == 0){
            music1.setChecked(true);
        }else if (music == 1) {
            music2.setChecked(true);
        }else if (music == 2) {
            music3.setChecked(true);
        }else if (music == 3) {
            music4.setChecked(true);
        }

        rgMusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.music1:
                        editor.putInt("music", 0);
                        editor.apply();
                        break;
                    case R.id.music2:
                        soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                        editor.putInt("music", 1);
                        editor.apply();
                        break;
                    case  R.id.music3:
                        soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                        editor.putInt("music", 2);
                        editor.apply();
                        break;
                    case  R.id.music4:
                        soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                        editor.putInt("music", 3);
                        editor.apply();
                        break;
                }
            }
        });

        //游戏难度设置
        RadioGroup rgDiff = findViewById(R.id.rgdiff);
        RadioButton diff1 = findViewById(R.id.diff1);//difficult
        RadioButton diff2 = findViewById(R.id.diff2);//medium
        RadioButton diff3 = findViewById(R.id.diff3);//easy

        int diff = sharedPreferences.getInt("diffLines", 0);
        if (diff == 3){
            diff1.setChecked(true);
        }else if (diff == 4) {
            diff2.setChecked(true);
        }else if (diff == 5) {
            diff3.setChecked(true);
        }

        rgDiff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.diff1:
                        editor.putInt("diffLines", 3);//difficult
                        editor.apply();
                        break;
                    case R.id.diff2:
                        editor.putInt("diffLines", 4);//medium
                        editor.apply();
                        break;
                    case  R.id.diff3:
                        editor.putInt("diffLines", 5);//easy
                        editor.apply();
                        break;
                }
            }
        });

        lines = sharedPreferences.getInt("diffLines", 0);
        Log.d("*******************************The Lines is :", String.valueOf(lines));


        //Setting the vibrator

        RadioGroup vibrator_on_off = findViewById(R.id.vibrator_on_off);
        RadioButton on_vibrator = findViewById(R.id.on_vibrator);
        RadioButton off_vibrator = findViewById(R.id.off_vibrator);
        editor.putBoolean("vibratorer", false);
        boolean vibratorer = sharedPreferences.getBoolean("vibratorer", false);
        if (vibratorer) {
            on_vibrator.setChecked(true);
        } else {
            off_vibrator.setChecked(true);
        }

        vibrator_on_off.setOnCheckedChangeListener((group,checked)->{
            if (checked == R.id.on_vibrator){

                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }
                //震动效果
                //milliseconds：振动的毫秒数。这必须是一个正数。
                //amplitude：振动的强度。它必须是1到255之间的值，或DEFAULT_AMPLITUDE。
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                editor.putBoolean("vibratorer", true);
                editor.commit();
            } else if (checked == R.id.off_vibrator) {
                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }
                editor.putBoolean("vibratorer", false);
                editor.commit();
            }
        });

//        btCreate = (Button) findViewById(R.id.btcreate);
//        btCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Creating the Intent of adding the shortcut
//                Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//                String title = getResources().getString(R.string.app_name);
//                //Loading the icon of the shortcut
//                Parcelable icon = Intent.ShortcutIconResource.fromContext(SettingActivity.this, R.drawable.newlogo);
//                //Operate the Intent after Creating the click shortcut. When you click the created shortcut, start the program again.
//                Intent myIntent = new Intent(SettingActivity.this, LoginActivity.class);
//                //Setting the title of the shortcut
//                addIntent.putExtra(Intent.EXTRA_COMPONENT_NAME, title);
//                //Setting the icon of the shortcut
//                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
//                //Setting the Intent corresponding to zhe shortcut
//                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
//                //Sending broadcast add the shortcut
//                sendBroadcast(addIntent);
//
//                if (sharedPreferences.getInt("music", 0) == 1){
//                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
//                }else if (sharedPreferences.getInt("music", 0) == 2) {
//                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
//                }else if (sharedPreferences.getInt("music", 0) == 3) {
//                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
//                }
//
////                if (sharedPreferences.getBoolean("vibratorer", true)) {
////                    震动效果
//                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
////                }
//            }
//        });

        //Cleaning the Data of the Rank
        clearRank = (Button) findViewById(R.id.clear_rank);
        clearRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                SqlTools.DeleteAll();
                Resources resource = getResources();
                String success = resource.getString(R.string.clear_rank_success);
                MainActivity.getMainActivity().saveBestScore(0);
                Toast.makeText(SettingActivity.this, success, Toast.LENGTH_SHORT).show();

                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }

//                if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//                }
               } catch (Exception e) {
                    Resources resource = getResources();
                    String delete = resource.getString(R.string.cleared_rank);
                    Toast.makeText(SettingActivity.this, delete, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Backing to the MenuActivity
        btBack4 = (Button) findViewById(R.id.btback4);
        btBack4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, MenuActivity.class);
                startActivity(intent);

//                if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//                }

                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }

                onStop();
            }
        });

    }
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
