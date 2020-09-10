package com.wdd.new2048;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GamePrinciple extends LinearLayout{
    MainActivity mainay = MainActivity.getMainActivity();
    private int lines = mainay.i;

    //Setting the whole grid of rows and columns
    public Card[][] cardsMap = new Card[lines][lines];
    //Useing  ArrayList Point to manage the empty point.
    public List<Point> emptyPoints = new ArrayList<Point>();

    //用于读
//    public SharedPreferences sharedPreferences;
    //用于写
//    public SharedPreferences.Editor editor;
    //定义一个震动变量
    public Vibrator vibrator;

    //Getting the String from the .xml file
    Resources resources = getResources();

    private static GamePrinciple gamePrinciple = null;

    public static GamePrinciple getGamePrinciple() {
        Log.d("GamePrinciple",gamePrinciple.toString());
        return gamePrinciple;
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GamePrinciple(Context context) {
        super(context);

        initGameprinciple();
    }

    @SuppressLint("LongLogTag")
    public GamePrinciple(Context context, AttributeSet attrs) {
        super(context, attrs);

        initGameprinciple();
    }

    //The realization method of gesture recognition
    public void initGameprinciple(){
        //setting overall layout
        setOrientation(LinearLayout.VERTICAL);

        //setting mouse touch listener, positioning according to coordinates can acoid misoperation
        setOnTouchListener(new OnTouchListener() {

            private float startX,startY,offsetX,offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

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
                                swipeLeft();
                            }else if (offsetX>5) {
                                swipeRight();
                            }
                        }else{
                            if (offsetY<-5) {
                                swipeUp();
                            }else if (offsetY>5) {
                                swipeDown();
                            }
                        }

                        break;
                }
                return true;
            }
        });


    }
    //Add card layout during layout drawing
    @SuppressLint("LongLogTag")
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Config.CARD_WIDTH = (Math.min(w,h)-10)/lines;//求卡片
        addCards(Config.CARD_WIDTH,Config.CARD_WIDTH);

        startGame(true);
    }

    //Adding card view
    private void addCards(int cardWidth,int cardHeight){

        Card c;

        LinearLayout line;
        LinearLayout.LayoutParams lineLp;

        for (int y = 0; y < lines; y++) {
            line = new LinearLayout(getContext());
            lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
            addView(line, lineLp);

            for (int x = 0; x < lines; x++) {
                c = new Card(getContext());
                line.addView(c, cardWidth, cardHeight);

                cardsMap[x][y] = c;
            }
        }
    }

    public void startGame(boolean flag){

        if (flag) {
            MainActivity aty = MainActivity.getMainActivity();
            aty.rangeTime = 0;
            aty.time.setBase(SystemClock.elapsedRealtime());
            aty.btnPgame.setText(resources.getString(R.string.pause));
            aty.clearScore();
            aty.showBestScore(aty.getBestScore());

            for (int y = 0; y < lines; y++) {
                for (int x = 0; x < lines; x++) {
                    cardsMap[x][y].setNum(0);
                }
            }

            //Adding three cards randomly
            addRandomNum();
            addRandomNum();
        } else {
            MainActivity aty = MainActivity.getMainActivity();
            aty.time.setBase(SystemClock.elapsedRealtime());
            aty.time.start();
            aty.btnPgame.setText(resources.getString(R.string.pause));
            aty.clearScore();
            aty.showBestScore(aty.getBestScore());

            for (int y = 0; y < lines; y++) {
                for (int x = 0; x < lines; x++) {
                    cardsMap[x][y].setNum(0);
                }
            }

            //Adding three cards randomly
            addRandomNum();
            addRandomNum();
        }

    }

    //The realization method of adding card
    private void addRandomNum(){
        emptyPoints.clear();
        //Collecting all blanks to the point List
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lines; x++) {
                if (cardsMap[x][y].getNum()<=0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }
        //Creating a card randomly
        if (emptyPoints.size()>0) {
            //Firstly,a coordinate is randomly obtained. Then take the point List of this coordinate from the blank point. Assign to p.
            //通过函数emptyPoints.remove((int) (Math.random() * emptyPoints.size())); math.random()来实现产生数字的坐标点，
            // 然后通过三目运算符来设置在此坐标上生成的数字是2还是4， 0.1 mean is 这里2和4产生的比例是9：1
            Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
            cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);

            //A card appears at this coordinate
            MainActivity.getMainActivity().getGameanimate().createScaleTo1(cardsMap[p.x][p.y]);
        }
    }


    /*
     * Moveing all the squares in the game in a certain direction to merge the squares of the same number.
     * There are four functions responsible for moving, namely up, down, left, and right.
     * */
    public void swipeLeft(){

        /*
        merge : Whether to merge cards
        1. Merge empty card with existing digital card
        2. Merge cards whose has the equal digit
        true is meaning move
        false is meaning false
         */
        boolean merge = false;

        for (int y = 0; y < lines; y++) {//All the current column
            for (int x = 0; x < lines; x++) {

                for (int x1 = x+1; x1 < lines; x1++) {
                    //If the right has the unempty card
                    if (cardsMap[x1][y].getNum()>0) {
                        //If the current coordinate is blank
                        if (cardsMap[x][y].getNum()<=0) {

                            //Moving the right card to the corrent coordinate, and making the Animation
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x1][y],cardsMap[x][y], x1, x, y, y);

                            //The current coordinate get the right card's number
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            //Making the right coordinate's cart to set 0
                            cardsMap[x1][y].setNum(0);

                            x--;
                            merge = true;

                        }
                        //The current card's number is equal to the right card's number
                        else if (cardsMap[x][y].equals(cardsMap[x1][y])) {

                            //Mergeing the right card to the corrent coordinate, and making the Animation
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
                            //changing the current card's number
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            //Making the right card's number to 0
                            cardsMap[x1][y].setNum(0);

                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }
        //All cards are moving
        if (merge) {
            //Randomly generate a digital card
            addRandomNum();
            //Checking the game whether is complete
            checkComplete();
        }
    }
    public void swipeRight(){

        boolean merge = false;

        for (int y = 0; y < lines; y++) {
            for (int x = lines-1; x >=0; x--) {

                for (int x1 = x-1; x1 >=0; x1--) {
                    if (cardsMap[x1][y].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    //向上移动
    public void swipeUp(){

        boolean merge = false;

        for (int x = 0; x < lines; x++) {
            for (int y = 0; y < lines; y++) {

                for (int y1 = y+1; y1 < lines; y1++) {
                    if (cardsMap[x][y1].getNum()>0) {
                        if (cardsMap[x][y].getNum()<=0) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y--;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    public void swipeDown(){

        boolean merge = false;

        for (int x = 0; x < lines; x++) {
            for (int y = lines-1; y >=0; y--) {

                for (int y1 = y-1; y1 >=0; y1--) {
                    if (cardsMap[x][y1].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            MainActivity.getMainActivity().getGameanimate().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }


    //Judge the current game whether complete or fail
    private void checkComplete(){
        boolean complete = true;
        ALL://Enhance for cycle
        //The game continues when has empty position or adjacent equal cards
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lines; x++) {
                if (cardsMap[x][y].getNum()==0||
                        (x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<lines-1&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<lines-1&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {
                    complete = false;
                    break ALL;
                }
            }
        }
        //Pop-up prompt for Game over
        if (complete) {

            MainActivity.getMainActivity().time.stop();

            //Setting the Dialog text
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.getMainActivity());
            dialog.setTitle(resources.getString(R.string.dialog_title));
            dialog.setMessage(resources.getString(R.string.dialog_message));
            dialog.setCancelable(false);
            dialog.setNegativeButton(resources.getString(R.string.restart), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame(false);
                }
            });
            dialog.setPositiveButton(resources.getString(R.string.exit),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int v) {
                    // TODO Auto-generated method stub
                    //遍历Activity集合，关闭所有bai集合内的Activity
                    for(int i=0;i<PublicWay.activityList.size();i++){
                        if (null != PublicWay.activityList.get(i)) {
                            PublicWay.activityList.get(i).finish();
                        }
                    }
                }
            } ).show();
            //向数据库中插入当前用户名、分数和当前游戏难度
            SqlTools.Insert(MainActivity.getMainActivity().username, MainActivity.getMainActivity().score, MainActivity.getMainActivity().i);

//            MainActivity mainActivity = new MainActivity();
//            String username = String.valueOf(mainActivity.etUsername);
//            int score = mainActivity.score;
//            SqlTools.Insert(username, score);
        }

    }


}