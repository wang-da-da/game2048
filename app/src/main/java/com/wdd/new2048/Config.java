package com.wdd.new2048;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.SoundPool;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Switch;

import java.util.HashMap;

public class Config {

//    public static final int LINES = 4;
    public static int CARD_WIDTH = 0;
    public static final String SP_KEY_BEST_SCORE = "bestScore";

    //Setting the Max word count
    public static class MyInputFilter implements InputFilter {

        private Paint mPaint;

        private int mMaxWidth;

        private static final String TAG = "MyInputFilter";

        private int EDIT_WIDTH = 150;

        private int mPadding = 10;

        public MyInputFilter(Paint paint, int maxWidth) {
            if (paint != null) {
                mPaint = paint;
            } else {
                mPaint = new Paint();
                mPaint.setTextSize(30F);
            }

            if (maxWidth > 0) {
                mMaxWidth = maxWidth - mPadding;
            } else {
                mMaxWidth = EDIT_WIDTH;
            }

        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            float w = mPaint.measureText(dest.toString() + source.toString());
            if (w > mMaxWidth) {
                //TODO: remind the user not to input anymore
                return "";
            }
            if (source.equals(" ") || source.toString().contentEquals("\n"))
                return "";

            return source;
        }

    }

}
