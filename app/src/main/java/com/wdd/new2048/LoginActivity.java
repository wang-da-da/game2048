package com.wdd.new2048;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    public static EditText username;
    private MyDatabaseHelper dbHelper;

    public Vibrator vibrator;

    //播放短小音频
    public SoundPool soundPool;
    public int[] soundResIds = {R.raw.ding, R.raw.baji, R.raw.shua};
    public HashMap<Integer, Integer> soundIdMap = new HashMap<>();

    //用于读
    private SharedPreferences sharedPreferences;
    //用于写
    private SharedPreferences.Editor editor;
    public int lines;

    private  MyService.MyBinder myBinder;
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        /*//没有title，在activity是不会出现标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //activity产生之前，就已经可以全屏的操作
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/

        //向应用中写入Game_Data文件
        sharedPreferences = getSharedPreferences("GameData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putBoolean("history", false);

        setContentView(R.layout.activity_login);

        PublicWay.activityList.add(this);

        //音乐初始化
        soundPool = new SoundPool.Builder().setMaxStreams(soundResIds.length).build();
        soundIdMap.put(0, soundPool.load(this, soundResIds[0], 1));
        soundIdMap.put(1, soundPool.load(this, soundResIds[1], 1));
        soundIdMap.put(2, soundPool.load(this, soundResIds[2], 1));

        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        //启动的时候连接数据库
        SqlTools.deHelper = new MyDatabaseHelper(this,"Rank.db",null,6);

        //设置音乐状态初始值
        editor.putInt("music", 0);
        //设置游戏初始难度
        editor.putInt("diffLines", 4);

        editor.apply();
        lines = sharedPreferences.getInt("diffLines", 0);
        Log.d("===========================Loginthe lines", String.valueOf(lines));

        //从xml文件里获取字符串
        Resources resources = getResources();
        String chinese = resources.getString(R.string.Chinese);
        String english = resources.getString(R.string.English);


        //setting the username's long
        username = (EditText) findViewById(R.id.username);
        int width = username.getWidth();
        Paint paint = new Paint();
        paint.setTextSize(username.getTextSize());
        InputFilter[] filters = { new  Config.MyInputFilter(paint, width)};
        username.setFilters(filters);

        //SQLite
        dbHelper = new MyDatabaseHelper(this, "Rank.db", null, 6);

//        SettingActivity.getSettingActivity().soundPool = new SoundPool.Builder().setMaxStreams(SettingActivity.getSettingActivity().soundResIds.length).build();

        //Realize interface jump
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View login) {
                dbHelper.getWritableDatabase();
//                editor.putBoolean("vibratorer",false);
//                editor.apply();
//                if (sharedPreferences.getBoolean("vibratorer", true)) {
//                    震动效果
                    vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//                }
//                if (sharedPreferences.getInt("music", 0) == 1){
//                    SettingActivity.getSettingActivity().soundPool.play(SettingActivity.getSettingActivity().soundIdMap.get(0), 1, 1, 1, 0, 1);
//                }
                String name = username.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Resources resource = getResources();
                    String empty = resource.getString(R.string.no_blank);
                    Toast.makeText(LoginActivity.this, empty, Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                }

                //对播放的音乐进行判断
                if (sharedPreferences.getInt("music", 0) == 1){
                    soundPool.play(soundIdMap.get(0), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 2) {
                    soundPool.play(soundIdMap.get(1), 1, 1, 1, 0, 1);
                }else if (sharedPreferences.getInt("music", 0) == 3) {
                    soundPool.play(soundIdMap.get(2), 1, 1, 1, 0, 1);
                }

                //Open the service
                Intent bindIntent = new Intent(LoginActivity.this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);

            }
        });


    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            myBinder.startMe();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /*
     * 在activity的 onResume()执行时，启用一个像素的悬浮窗防止应用被清理
     * */
    @Override
    protected void onResume() {
        super.onResume();

        Button button = new Button(getApplicationContext());
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        /**
         * 以下都是WindowManager.LayoutParams的相关属性 具体用途请参考SDK文档
         */
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; // 这里是关键
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        /**
         * 这里的flags也很关键 代码实际是wmParams.flags |=FLAG_NOT_FOCUSABLE;
         * 40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        wmParams.width = 1;
        wmParams.height = 1;
//        wm.addView(button, wmParams); // 创建View

    }


    //装填菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //使用xml文件编辑菜单
        //getMenuInflater().inflate(R.menu.main,menu);

        menu.add(0,R.id.select_chinese,0,R.string.Chinese);
        menu.add(0,R.id.select_english,0,R.string.English);
        return true;
    }

    //菜单触发事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //获取读写对象
        sharedPreferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        switch (item.getItemId()){

            //根据菜单，选择不同语言，
            //选择后在sharedpreferences里保存选项，true为中文,false为英文
            case R.id.select_chinese :
                changeLanguage(Locale.CHINA);break;
            case R.id.select_english:
                changeLanguage(Locale.US);break;
            default:break;
        }
        return true;
    }

    //根据触发事件修改语言
    public void changeLanguage(Locale locale){
//        获取屏幕信息：屏幕密度、高度、像素、字体等信息
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //获取配置文件
        Configuration configuration = getResources().getConfiguration();

        //if the SDK version is more than the 17
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
//            为配置对象重新设置语言
            configuration.setLocale(locale);
        }else{
            configuration.locale = locale;
        }
        getResources().updateConfiguration(configuration,displayMetrics);
        //在SharedPreferences里保存操作

        //重启activity
        Bundle bundle = new Bundle();
//        bundle.putSerializable("username",LoginActivity.username.getText().toString());
        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}