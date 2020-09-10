package com.wdd.new2048;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class GameAnimate extends FrameLayout {

    /*Useing ArrayList cards to manage the creation and recycling of temporary cards (to avoid creating new objects every time a temporary card is created);
    create a temporary card, move from "card from" to "card to", and make the temporart card invisible after the animation is completed.
    And use "cards" to recycle the card*/
    private List<Card> cards = new ArrayList<Card>();

    public GameAnimate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayer();
    }

    public GameAnimate(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayer();
    }

    public GameAnimate(Context context) {
        super(context);
        initLayer();
    }

    private void initLayer(){
    }

    //The realization method of card move
    public void createMoveAnim(final Card from,final Card to,int fromX,int toX,int fromY,int toY){

        //"card from" is the temporary card
        final Card c = getCard(from.getNum());

        //setting layout
        LayoutParams lp = new LayoutParams(Config.CARD_WIDTH, Config.CARD_WIDTH);
        lp.leftMargin = fromX * Config.CARD_WIDTH;
        lp.topMargin = fromY * Config.CARD_WIDTH;
        c.setLayoutParams(lp);

        //"caro to" 's number is 0, making it invisible
        if (to.getNum()<=0) {
            to.getCard().setVisibility(View.INVISIBLE);
        }
        //The realization method of translate animation. Moving from "card from" to "card to"
        TranslateAnimation ta = new TranslateAnimation(0, Config.CARD_WIDTH*(toX-fromX), 0, Config.CARD_WIDTH*(toY-fromY));
        ta.setDuration(100);
        ta.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            //To recycle card when the Animation is over
            @Override
            public void onAnimationEnd(Animation animation) {
                to.getCard().setVisibility(View.VISIBLE);
                recycleCard(c);
            }
        });
        c.startAnimation(ta);
    }

    //The realization method of creat card
    private Card getCard(int num){
        Card c;
        if (cards.size()>0) {
            //remove one temporary card from cards
            c = cards.remove(0);
        }else{
            c = new Card(getContext());
            addView(c);
        }
        c.setVisibility(View.VISIBLE);
        c.setNum(num);
        return c;
    }

    //The realization method of recycle card
    private void recycleCard(Card c){
        //setting the current card is invisible, and adding it to the cards
        c.setVisibility(View.INVISIBLE);
        c.setAnimation(null);
        cards.add(c);
    }

    //The realization method of card appears
    public void createScaleTo1(Card target){

         /*
                AnimationSet相当于一个动画的集合，true表示使用Animation的interpolator
                false则是使用自己的。
                Interpolator 被用来修饰动画效果，定义动画的变化率，可以使存在的动画效果
                accelerated(加速)，decelerated(减速),repeated(重复),bounced(弹跳)等。
             */
            /*
                参数解释：
                    第一个参数：X轴水平缩放起始位置的大小（fromX）。1代表正常大小
                    第二个参数：X轴水平缩放完了之后（toX）的大小，0代表完全消失了
                    第三个参数：Y轴垂直缩放起始时的大小（fromY）
                    第四个参数：Y轴垂直缩放结束后的大小（toY）
                    第五个参数：pivotXType为动画在X轴相对于物件位置类型
                    第六个参数：pivotXValue为动画相对于物件的X坐标的开始位置
                    第七个参数：pivotXType为动画在Y轴相对于物件位置类型
                    第八个参数：pivotYValue为动画相对于物件的Y坐标的开始位置
                   （第五个参数，第六个参数），（第七个参数,第八个参数）是用来指定缩放的中心点
                    0.5f代表从中心缩放
                    Animation.ABSOLUTE:默认值，围绕设置动画控件的左上角旋转与pivotXValue = 0,pivotYVaule = 0效果相同
                    Animation.RELATIVE_TO_SELF:设置动画控件的左上角为坐标原点(0，0)，动画旋转轴的坐标为(view.getWidth*pivotXValue,view.getHeight*pivotYValue)
                         负数向左(X轴)/上(Y轴)偏移，正数向右(X轴)/下(Y轴)偏移;
                    Animation.RELATIVE_TO_PARENT:设置动画控件的左上角为坐标原点(0，0)，动画旋转轴的坐标为(parent.getWidth*pivotXValue,parent.getHeight*pivotYValue)
                        【parent为view的父控件】负数向左(X轴)/上(Y轴)偏移，正数向右(X轴)/下(Y轴)偏移;
             */

        //Implementation and parameters of zoom function
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getCard().startAnimation(sa);
    }

}
