package com.example.huc_project.ui.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import com.example.huc_project.R;

public class PaintText extends View {
    public String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };
    private int which_one;
    private Path myArc;
    private Paint mPaintText;

    public PaintText(Context context, int text , int margin_left, int margin_top, int margin_right, int margin_bottom, int angle_start, int angle_end) {
        super(context);
        //create Path object
        myArc = new Path();
        which_one = text;
        //create RectF Object
        RectF oval = new RectF(margin_left, margin_top, margin_right, margin_bottom);
        //add Arc in Path with start angle and sweep angle
        myArc.addArc(oval, angle_start, angle_end);
        //create paint object
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        //set style
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        //set color
        mPaintText.setColor(getResources().getColor(R.color.textColor));
        //set text Size
        mPaintText.setTextSize(45f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw Text on Canvas
        canvas.drawTextOnPath(Text[which_one], myArc, 0, 20, mPaintText);
        invalidate();
    }

}