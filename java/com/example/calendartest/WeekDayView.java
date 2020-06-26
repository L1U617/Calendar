package com.example.calendartest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class WeekDayView extends View {
    private int color = Color.parseColor("#385466");
    private int mWeekSize = 20;

    private Typeface tf;

    private Paint paint;
    private DisplayMetrics mDisplayMetrics;
    private String[] weekString = new String[]{"SUN","MON","TUE","WED","THU","FRI","SAT"};

    public WeekDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        paint = new Paint();
    }

    public void setTf(Typeface font){
        tf = font;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = mDisplayMetrics.densityDpi * 30;
        }
        if(widthMode == MeasureSpec.AT_MOST){
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
        paint.setTypeface(tf);
        int columnWidth = width / 7;
        for(int i=0;i < weekString.length;i++){
            String text = weekString[i];
            int fontWidth = (int) paint.measureText(text);
            int startX = columnWidth * i + (columnWidth - fontWidth)/2;
            int startY = (int) (height/2 - (paint.ascent() + paint.descent())/2);
            paint.setColor(color);
            canvas.drawText(text, startX, startY, paint);
        }
    }
}
