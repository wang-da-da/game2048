package com.wdd.new2048;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public int score = 0;
    public TextView tvScore,tvBestScore;
    private LinearLayout root = null;
    private Button btnGame;
    public Button btnPgame;
    public Button btnNewGame;
    private Button btBack2;
    private GamePrinciple gamePrinciple;
    private GameAnimate gameAnimate = null;
    public TextView etUsername;

    public String username;

    //setting chronometer
    public Chronometer time;
    private boolean PAUSE=false;
    public long  rangeTime;

    //定义读写变量
    //用于读
    private SharedPreferences sharedPreferences;
    //用于写
    private SharedPreferences.Editor editor;
    public Vibrator vibrator;

    public int lines;
    public int i;

    //播放短小音频
    public SoundPool soundPool;
    public int[] soundResIds = {R.raw.ding, R.raw.baji, R.raw.shua};
    public HashMap<Integer, Integer> soundIdMap = new HashMap<>();

    //设立一个标识，判定是否调用了历史记录
    public static boolean bingo;

    private static MainActivity mainActivity = null;

    public static MainActivity getMainActivity() {

//        Log.d("MainActivity",mainActivity.toString());
        return mainActivity;
    }

    public MainActivity() {
        mainActivity = this;
    }
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取读写对象
        sharedPreferences = getSharedPreferences("GameData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        lines = sharedPreferences.getInt("diffLines", 0);
        i = lines;
        Log.d("*******************************MAinThe Lines is :", String.valueOf(i));

        //音乐初始化
        soundPool = new SoundPool.Builder().setMaxStreams(soundResIds.length).build();
        soundIdMap.put(0, soundPool.load(this, soundResIds[0], 1));
        soundIdMap.put(1, soundPool.load(this, soundResIds[1], 1));
        soundIdMap.put(2, soundPool.load(this, soundResIds[2], 1));

        setContentView(R.layout.activity_main);

        //启动的时候连接数据库
        SqlTools.deHelper = new MyDatabaseHelper(this,"Rank.db",null,6);

        //Getting the String from the .xml file
        final Resources resources = getResources();
        PublicWay.activityList.add(this);

        //The game main layout
        root = (LinearLayout) findViewById(R.id.container);

        //Getting Login.edittext text
        etUsername = (TextView) findViewById(R.id.etusername);
        username = LoginActivity.username.getText().toString();
        etUsername.setText(resources.getString(R.string.username) + username);

        tvScore = (TextView) findViewById(R.id.tvScore);
        tvBestScore = (TextView) findViewById(R.id.tvBestScore);

        gamePrinciple = (GamePrinciple) findViewById(R.id.gameprinciple);

        time = (Chronometer) findViewById(R.id.time);

        btnGame = (Button) findViewById(R.id.btnGame);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btngame) {
                rangeTime = 0;
                gamePrinciple.startGame(true);
                //setting start time.  SystemClock is obtaining system time.
                //time.setBase(SystemClock.elapsedRealtime());
                //start chronometer
                time.start();
                btnGame.setEnabled(false);

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
        btnPgame = (Button) findViewById(R.id.btnPgame);
        btnPgame.setOnClickListener(new View.OnClickListener() {
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

                if(!PAUSE) {
                    btnPgame.setText(resources.getString(R.string.go_on));
                    MainActivity.this.rangeTime = SystemClock.elapsedRealtime() - time.getBase();
                    time.stop();
                    onPause();
                }else {
                    btnPgame.setText(resources.getString(R.string.pause));
                    time.setBase(SystemClock.elapsedRealtime()-rangeTime);
                    time.start();
                    onPause();
                }
                PAUSE =! PAUSE;
            }
        });
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {
            // MainActivity.getMainActivity().time.setBase(SystemClock.elapsedRealtime());
            gamePrinciple.startGame(false);
//            etUsername.setText(resources.getString(R.string.username) + LoginActivity.username.getText().toString());
//            rangeTime = 0;
            btnPgame.setText(resources.getString(R.string.pause));

//            if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//            }

            //对播放的音乐进行判断
            if (sharedPreferences.getInt("music", 0) == 1){
                soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 2) {
                soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 3) {
                soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
            }

        }});

        gameAnimate = (GameAnimate) findViewById(R.id.gameanimate);

//        //Backing to the MenuActivity
//        btBack2 = (Button) findViewById(R.id.btback2);
//        btBack2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                if (sharedPreferences.getBoolean("vibratorer", true)) {
////                    震动效果
//                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
////                }
//
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, MenuActivity.class);
//                startActivity(intent);
//            }
//        });

        if (sharedPreferences.getBoolean("history", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.dialog_title))
                    .setMessage(R.string.dialog_continue)
                    .setCancelable(false);
            setSaveButton2(builder);
            setNotSaveButton2(builder).create().show();
        }

    }

    public void clearScore(){
        score = 0;
        showScore();
    }

    public void showScore(){

        tvScore.setText(score + "");
    }

    public void addScore(int s){
        score += s;
        showScore();

        int maxScore = Math.max(score, getBestScore());
        saveBestScore(maxScore);
        showBestScore(maxScore);
    }

    /*  SharedPreferences：以Map形式存放简单的配置参数；
        ContentProvider：将应用的私有数据提供给其他应用使用；
        文件存储：以IO流形式存放，可分为手机内部和手机外部（sd卡等）存储，可存放较大数据；
        SQLite：轻量级、跨平台数据库，将所有数据都是存放在手机上的单一文件内，占用内存小；
        网络存储 ：数据存储在服务器上，通过连接网络获取数据；
    */

    //SharedPreferences：以Map形式存放简单的配置参数；
    public void saveBestScore(int s){
        /*
            Context.MODE_PRIVATE: 指定该SharedPreferences数据只能被本应用程序读、写；
            Context.MODE_WORLD_READABLE:  指定该SharedPreferences数据能被其他应用程序读，但不能写；
            Context.MODE_WORLD_WRITEABLE:  指定该SharedPreferences数据能被其他应用程序写；
        */
        Editor e = getPreferences(MODE_PRIVATE).edit();
        e.putInt(Config.SP_KEY_BEST_SCORE, s);
        e.commit();
    }

    public int getBestScore(){
        return getPreferences(MODE_PRIVATE).getInt(Config.SP_KEY_BEST_SCORE, 0);
    }

    public void showBestScore(int s){

        tvBestScore.setText(s + "");
    }

    public GameAnimate getGameanimate() {

        //对播放的音乐进行判断
        if (sharedPreferences.getInt("music", 0) == 1){
            soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
        }else if (sharedPreferences.getInt("music", 0) == 2) {
            soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
        }else if (sharedPreferences.getInt("music", 0) == 3) {
            soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
        }
        return gameAnimate;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "is on create !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        if (!PAUSE) {

            this.gamePrinciple.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
        else {
            this.gamePrinciple.setOnTouchListener(new View.OnTouchListener() {
                private float startX,startY,offsetX,offsetY;
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        //Pressed coordinate of mouse
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            break;
                        //Raised coordinate of mouse
                        case MotionEvent.ACTION_UP:
                            offsetX = event.getX()-startX;
                            offsetY = event.getY()-startY;

                            //Judge the direction of movement by changeing the coordinate values
                            //Math.abs return the |x|
                            if (Math.abs(offsetX)>Math.abs(offsetY)) {
                                if (offsetX<-5) {
                                    gamePrinciple.swipeLeft();
                                }else if (offsetX>5) {
                                    gamePrinciple.swipeRight();
                                }
                            }else{
                                if (offsetY<-5) {
                                    gamePrinciple.swipeUp();
                                }else if (offsetY>5) {
                                    gamePrinciple.swipeDown();
                                }
                            }

                            break;
                    }
                    return true;
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "is on create ==========================================================");
        if (!bingo) {
            if (sharedPreferences.getInt("score", 0) > 0) {
                username = sharedPreferences.getString("name", username);
                score = sharedPreferences.getInt("score", 0);
                rangeTime = sharedPreferences.getLong("time", 0);

                Log.d("bingo_username",username);
                Log.d("bingo_score", String.valueOf(score));
                Log.d("bingo_rangtime", String.valueOf(rangeTime));
            }
        }
    }

    //重写返回键按钮
    @Override
    public void onBackPressed() {

        //提前修改数据并提交保存
        sharedPreferences = getSharedPreferences("GameData",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("name", username);
        editor.putInt("score", score);
        rangeTime = SystemClock.elapsedRealtime() - time.getBase();
        editor.putLong("time", rangeTime);
        //把标记置为true，下次打开的时候使用历史记录
        editor.putBoolean("history",true);
        editor.commit();
        Log.d("onback_username",username);
        Log.d("onback_score", String.valueOf(score));
        Log.d("onback_rangtime", String.valueOf(rangeTime));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                //设置标题
                .setTitle(R.string.dialog_title)
                //设置文本
                .setMessage(R.string.dialog_savegame)
                .setCancelable(false);

        //添加退出游戏按钮
        setNotSaveButton(builder);
        //添加保存游戏按钮
        setSaveButton(builder).create().show();
    }

    /**
     * 给返回键对话框添加确定按钮（保存游戏）
     */
    private android.app.AlertDialog.Builder setSaveButton(android.app.AlertDialog.Builder builder){
        return builder.setNegativeButton(R.string.save,(dialog,which)->{

            editor.putString("name", username);
            editor.putInt("score", score);
            rangeTime = SystemClock.elapsedRealtime() - time.getBase();
            editor.putLong("time", rangeTime);
            editor.commit();
            Log.d("save_username",username);
            Log.d("save_score", String.valueOf(score));
            Log.d("save_rangtime", String.valueOf(rangeTime));
            //关闭主线程
            MainActivity.this.finish();
//            if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//            }
            //对播放的音乐进行判断
            if (sharedPreferences.getInt("music", 0) == 1){
                soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 2) {
                soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 3) {
                soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
            }
        });
    }

    /**
     * 给返回键对话框添加取消按钮  不保存游戏（退出游戏）
     */
    private android.app.AlertDialog.Builder setNotSaveButton(android.app.AlertDialog.Builder builder){
        return builder.setPositiveButton(R.string.no,(dialog,which)->{

            sharedPreferences = getSharedPreferences("GameData",Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
//            editor.clear();
            editor.putBoolean("history", false);
            editor.commit();
            Log.d("exit_username",username);
            Log.d("exit_score", String.valueOf(score));
            Log.d("exit_rangtime", String.valueOf(rangeTime));

            //关闭弹窗
            dialog.dismiss();
            //关闭主线程
            MainActivity.this.finish();

//            if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//            }
        });
    }


//    AlertDialog.Load 登入游戏时，载入游戏
    private AlertDialog.Builder setSaveButton2(AlertDialog.Builder builder) {
        return builder.setNegativeButton(R.string.yes, (dialog,which)->{

            final Resources resources = getResources();
            //Loding the Game Data
            username = sharedPreferences.getString("name", username);
            etUsername.setText(resources.getString(R.string.username) + username);
            score = sharedPreferences.getInt("score", 0);
            tvScore.setText(score + "");
            rangeTime = sharedPreferences.getLong("time", 0);

            time.setBase(SystemClock.elapsedRealtime()-rangeTime);
            time.start();
            btnGame.setEnabled(false);
            Log.d("load_username",username);
            Log.d("load_score", String.valueOf(score));
            Log.d("load_rangtime", String.valueOf(rangeTime));
//            int cardx = sharedPreferences.getInt("cardx", 0);
//            int cardy = sharedPreferences.getInt("cardy", 0);
            //useing the history of recording
            bingo = true;

            editor.putBoolean("history", false);
            editor.commit();

            dialog.dismiss();

//            if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//            }
            //对播放的音乐进行判断
            if (sharedPreferences.getInt("music", 0) == 1){
                soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 2) {
                soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
            }else if (sharedPreferences.getInt("music", 0) == 3) {
                soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
            }
        });
    }

//    ALertDialog.NoLoad
    private AlertDialog.Builder setNotSaveButton2(AlertDialog.Builder builder) {
        return builder.setPositiveButton(R.string.no, (dialog,which)->{
            bingo = false;
//            editor.clear();
            editor.putBoolean("history", false);
            editor.commit();
            dialog.dismiss();

//            if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//            }
        });
    }

}