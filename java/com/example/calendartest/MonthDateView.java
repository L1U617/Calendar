package com.example.calendartest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.calendartest.DateUtils;
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
    private Paint mPaint;
    private int mDayColor = Color.parseColor("#000000");
    private int mSelectDayColor = Color.parseColor("#ffffff");
    private int mSelectBGColor = Color.parseColor("#1FC2F3");
    private int mCurrentColor = Color.parseColor("#ff0000");

    private int[] colorlist = {Color.parseColor("#97FFFF"), Color.parseColor("#8DEEEE"),
                                Color.parseColor("#79CDCD"), Color.parseColor("#00CDCD"), Color.parseColor("#FF0000")};

    private int mCurrYear,mCurrMonth,mCurrDay;
    private int mSelYear,mSelMonth,mSelDay;
    private int mColumnSize,mRowSize;

    private int mRadius;

    private DisplayMetrics mDisplayMetrics;
    private int mDaySize = 18;
    private TextView tv_date,tv_week;
    private int weekRow;
    private int [][] daysString;
    private int mCircleRadius = 6;
    private DateClick dateClick;
    private int mCircleColor = Color.parseColor("#ff0000");
    private Map<Integer, List< Map<String, Object> >> daysHasThingList;
    public MonthDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        Calendar calendar = Calendar.getInstance();
        mPaint = new Paint();
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
        mPaint.setTextSize(mDaySize*mDisplayMetrics.scaledDensity);
        String dayString;
        int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
        Log.d("DateView", "DateView:" + mSelMonth+"月1号周" + weekNumber);
        for(int day = 0;day < mMonthDays;day++){
            dayString = (day + 1) + "";
            int column = (day+weekNumber - 1) % 7;
            int row = (day+weekNumber - 1) / 7;
            daysString[row][column]=day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString))/2);
            int startY = (int) (mRowSize * row + mRowSize/2 - (mPaint.ascent() + mPaint.descent())/2);
            int cx = startX + (int)mPaint.measureText(dayString) / 2;
            int cy = startY - (int)mPaint.measureText(dayString) / 2;
            if(dayString.equals(mSelDay+"")){
                //绘制背景色矩形

                mPaint.setColor(mSelectBGColor);
                canvas.drawCircle(cx, cy, mRadius, mPaint);
                //记录第几行，即第几周
                weekRow = row + 1;
            }
            //绘制事务圆形标志
            drawColor(row,column, cx, cy,day + 1,canvas);

            if(dayString.equals(mSelDay+"")){
                mPaint.setColor(mSelectDayColor);
            }else if(dayString.equals(mCurrDay+"") && mCurrDay != mSelDay && mCurrMonth == mSelMonth){
                //正常月，选中其他日期，则今日为红色
                mPaint.setColor(mCurrentColor);
            }else{
                mPaint.setColor(mDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            if(tv_date != null){
                tv_date.setText(mSelYear + "年" + (mSelMonth + 1) + "月");
            }

            if(tv_week != null){
                tv_week.setText("第" + weekRow +"周");
            }
        }
    }

    private void drawColor(int row,int column,int cx, int cy, int day,Canvas canvas){
        if(daysHasThingList != null && daysHasThingList.size() >0){
            if(!daysHasThingList.containsKey(day)) return;
            int number = daysHasThingList.get(day).size();
            int circleColor;
            if(number < 5){
                circleColor = colorlist[number-1];
            }
            else circleColor = colorlist[4];

            mPaint.setColor(circleColor);
            canvas.drawCircle(cx, cy, mRadius, mPaint);
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

    /**
     * 初始化列宽行高
     */
    private void initSize(){
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
        if(mColumnSize < mRowSize){
            mRadius = mColumnSize / 2;
        }
        else mRadius = mRowSize / 2;
    }

    /**
     * 设置年月
     * @param year
     * @param month
     */
    private void setSelectYearMonth(int year,int month,int day){
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }
    /**
     * 执行点击事件
     * @param x
     * @param y
     */
    private void doClickAction(int x,int y){
        int row = y / mRowSize;
        int column = x / mColumnSize;
        setSelectYearMonth(mSelYear,mSelMonth,daysString[row][column]);
        invalidate();
        //执行activity发送过来的点击处理事件
        if(dateClick != null){
            dateClick.onClickOnDate();
        }
    }

    /**
     * 左点击，日历向后翻页
     */
    public void onLeftClick(){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 0){//若果是1月份，则变成12月份
            year = mSelYear-1;
            month = 11;
        }else if(DateUtils.getMonthDays(year, month) == day){
            //如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
            month = month-1;
            day = DateUtils.getMonthDays(year, month);
        }else{
            month = month-1;
        }
        setSelectYearMonth(year,month,day);
        invalidate();
    }

    /**
     * 右点击，日历向前翻页
     */
    public void onRightClick(){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 11){//若果是12月份，则变成1月份
            year = mSelYear+1;
            month = 0;
        }else if(DateUtils.getMonthDays(year, month) == day){
            //如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
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

    public void setmDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }


    public void setmSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    public void setmSelectBGColor(int mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    public void setmCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    public void setmDaySize(int mDaySize) {
        this.mDaySize = mDaySize;
    }

    public void setTextView(TextView tv_date,TextView tv_week){
        this.tv_date = tv_date;
        this.tv_week = tv_week;
        invalidate();
    }

    public void setDaysHasThingList(Map<Integer, List< Map<String, Object> >> daysHasThingList) {
        this.daysHasThingList = daysHasThingList;
    }


    public void setmCircleRadius(int mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }


    public void setmCircleColor(int mCircleColor) {
        this.mCircleColor = mCircleColor;
    }


    public interface DateClick{
        public void onClickOnDate();
    }

    public void setDateClick(DateClick dateClick) {
        this.dateClick = dateClick;
    }

    public void setTodayToView(){
        setSelectYearMonth(mCurrYear,mCurrMonth,mCurrDay);
        invalidate();
    }
}
