package com.wdd.new2048;



import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

    private TextView card;
    private View backGround;

    public Card(Context context) {
        super(context);

        LayoutParams lp = null;

        //card's background color and layout
        backGround = new View(getContext());
        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        backGround.setBackgroundColor(0x33ffffff);
        addView(backGround, lp);

        //card's number size and layout
        card = new TextView(getContext());
        card.setTextSize(22);
        card.setGravity(Gravity.CENTER);

        lp = new LayoutParams(-1, -1);
        lp.setMargins(10, 10, 0, 0);
        addView(card, lp);

        setNum(0);
    }


    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        //blanks
        if (num<=0) {
            card.setText("");
        }
        //cards
        else{
            card.setText(num + "");
        }

        switch (num) {
            case 0:
                card.setBackgroundColor(0x00000000);
                break;
            case 2:
                card.setBackgroundColor(0xffeee4da);
                break;
            case 4:
                card.setBackgroundColor(0xffede0c8);
                break;
            case 8:
                card.setBackgroundColor(0xfff2b179);
                break;
            case 16:
                card.setBackgroundColor(0xfff59563);
                break;
            case 32:
                card.setBackgroundColor(0xfff67c5f);
                break;
            case 64:
                card.setBackgroundColor(0xfff65e3b);
                break;
            case 128:
                card.setBackgroundColor(0xffedcf72);
                break;
            case 256:
                card.setBackgroundColor(0xffedcc61);
                break;
            case 512:
                card.setBackgroundColor(0xffedc850);
                break;
            case 1024:
                card.setBackgroundColor(0xffedc53f);
                break;
            case 2048:
                card.setBackgroundColor(0xffedc22e);
                break;
            default:
                card.setBackgroundColor(0xff3c3a32);
                break;
        }
    }

    //Determine whether the values of two cards are euqal
    public boolean equals(Card o) {

        return getNum()==o.getNum();
    }

    protected Card clone(){
        Card c= new Card(getContext());
        c.setNum(getNum());
        return c;
    }

    public TextView getCard() {
        return card;
    }

}

