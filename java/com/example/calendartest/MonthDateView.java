package com.example.calendartest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.calendartest.DateUtils;

import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MonthDateView extends View {
    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private int mDayColor = Color.parseColor("#385466");
    private int mSelectBGColor = Color.parseColor("#f8c060");
    private int mCurrentColor = Color.parseColor("#f8c060");

    private int eventColor = Color.parseColor("#70989a");

    private int mCurrYear,mCurrMonth,mCurrDay;
    private int mSelYear,mSelMonth,mSelDay;
    private int mColumnSize,mRowSize;

    private int mRadius;
    private Typeface tf;

    private DisplayMetrics mDisplayMetrics;
    private int mDaySize = 18;
    private TextView tv_date,tv_week;
    private int weekRow;
    private int [][] daysString;
    private DateClick dateClick;
    private Map<String, List< Map<String, String> >> daysHasThingList;
    public MonthDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        setSelectYearMonth(mCurrYear,mCurrMonth,mCurrDay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if(widthMode == MeasureSpec.AT_MOST){
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        daysString = new int[6][7];
        Paint textPaint = new Paint();
        Paint paint = new Paint();
        textPaint.setTextSize(mDaySize*mDisplayMetrics.scaledDensity);
        String dayString;
        String dateString;

        int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for(int day = 0;day < mMonthDays;day++){
            dayString = (day + 1) + "";
            int column = (day+weekNumber - 1) % 7;
            int row = (day+weekNumber - 1) / 7;
            daysString[row][column]=day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - textPaint.measureText(dayString))/2);
            int startY = (int) (mRowSize * row + mRowSize/2 - (textPaint.ascent() + textPaint.descent())/2);
            int cx = startX + (int)textPaint.measureText(dayString) / 2;
            int cy = startY - 17;
            if(dayString.equals(mSelDay+"") && mSelMonth == mCurrMonth && mCurrYear == mSelYear){
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(mSelectBGColor);
                canvas.drawCircle(cx, cy, mRadius, paint);
                weekRow = row + 1;
            }

            dateString = DateUtils.Format(mSelYear, mSelMonth + 1, day + 1);
            drawColor(row,column, cx, cy, dateString, canvas);

            if(dayString.equals(mCurrDay+"") && mCurrDay != mSelDay && mCurrMonth == mSelMonth){
                textPaint.setColor(mCurrentColor);
            }else{
                textPaint.setColor(mDayColor);
            }
            textPaint.setTypeface(tf);
            canvas.drawText(dayString, startX, startY, textPaint);

            if(tv_date != null){
                tv_date.setText(mSelYear + "-" + (mSelMonth + 1) + "-" + mSelDay);
            }

            if(tv_week != null){
                tv_week.setText("Week " + weekRow);
            }
        }
    }

    private void drawColor(int row,int column,int cx, int cy, String day,Canvas canvas){
        Paint paint = new Paint();
        if(daysHasThingList != null && daysHasThingList.size() >0){
            if(!daysHasThingList.containsKey(day)) return;
            int number = daysHasThingList.get(day).size();
            System.out.println(number);
            paint.setColor(eventColor);
            if(number < 5)
                paint.setAlpha(number * 50);
            canvas.drawCircle(cx, cy, mRadius, paint);
        }
    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int downX = 0,downY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode= event.getAction();
        switch(eventCode){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if(Math.abs(upX-downX) < 10 && Math.abs(upY - downY) < 10){//点击事件
                    performClick();
                    doClickAction((upX + downX)/2,(upY + downY)/2);
                }
                break;
        }
        return true;
    }

    private void initSize(){
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
        if(mColumnSize > mRowSize){
            mRadius = mRowSize / 2 - 12;
        }
        else mRadius = mColumnSize / 2 - 12;
    }

    private void setSelectYearMonth(int year,int month,int day){
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    private void doClickAction(int x,int y){
        int row = y / mRowSize;
        int column = x / mColumnSize;
        setSelectYearMonth(mSelYear,mSelMonth,daysString[row][column]);
        invalidate();
        if(dateClick != null){
            dateClick.onClickOnDate();
        }
    }

    public void setTf(Typeface font){
        tf = font;
    }
    public void onLeftClick(){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 0){
            year = mSelYear-1;
            month = 11;
        }else if(DateUtils.getMonthDays(year, month) == day){
            month = month-1;
            day = DateUtils.getMonthDays(year, month);
        }else{
            month = month-1;
        }
        setSelectYearMonth(year,month,day);
        invalidate();
    }
    public void onRightClick(){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 11){
            year = mSelYear+1;
            month = 0;
        }else if(DateUtils.getMonthDays(year, month) == day){
            month = month + 1;
            day = DateUtils.getMonthDays(year, month);
        }else{
            month = month + 1;
        }
        setSelectYearMonth(year,month,day);
        invalidate();
    }

    public int getmSelYear() {
        return mSelYear;
    }
    public int getmSelMonth() {
        return mSelMonth;
    }

    public int getmSelDay() {
        return this.mSelDay;
    }

    public void setTextView(TextView tv_date,TextView tv_week){
        this.tv_date = tv_date;
        this.tv_week = tv_week;
        invalidate();
    }

    public void setDaysHasThingList(Map<String, List< Map<String, String> >> daysHasThingList) {
        this.daysHasThingList = daysHasThingList;
    }

    public interface DateClick{
        public void onClickOnDate();
    }

    public void setDateClick(DateClick dateClick) {
        this.dateClick = dateClick;
    }

}
