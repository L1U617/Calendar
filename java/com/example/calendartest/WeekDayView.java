package com.example.calendartest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class WeekDayView extends View {
    private int mTopLineColor = Color.parseColor("#CCE4F2");
    //下横线颜色
    private int mBottomLineColor = Color.parseColor("#CCE4F2");
    //周一到周五的颜色
    private int mWeedayColor = Color.parseColor("#1FC2F3");
    //周六、周日的颜色
    private int mWeekendColor = Color.parseColor("#fa4451");
    //线的宽度
    private int mStrokeWidth = 4;
    private int mWeekSize = 14;
    private Paint paint;
    private DisplayMetrics mDisplayMetrics;
    private String[] weekString = new String[]{"SUN","MON","TUE","WED","THU","FRI","SAT"};
    public WeekDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        paint = new Paint();
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
        //进行画上下线
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mTopLineColor);
        paint.setStrokeWidth(mStrokeWidth);
        canvas.drawLine(0, 0, width, 0, paint);

        //画下横线
        paint.setColor(mBottomLineColor);
        canvas.drawLine(0, height, width, height, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
        int columnWidth = width / 7;
        for(int i=0;i < weekString.length;i++){
            String text = weekString[i];
            int fontWidth = (int) paint.measureText(text);
            int startX = columnWidth * i + (columnWidth - fontWidth)/2;
            int startY = (int) (height/2 - (paint.ascent() + paint.descent())/2);
            if(text.indexOf("SUN") > -1|| text.indexOf("SAT") > -1){
                paint.setColor(mWeekendColor);
            }else{
                paint.setColor(mWeedayColor);
            }
            canvas.drawText(text, startX, startY, paint);
        }
    }
}
