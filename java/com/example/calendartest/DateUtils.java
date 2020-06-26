package com.example.calendartest;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){
                    return 29;
                }else{
                    return 28;
                }
            default:
                return  -1;
        }
    }
    public static int getFirstDayWeek(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String Format(int year, int month, int day){
        String date = year + "-" + month + "-" + day;
        return date;
    }

    public static String Process(String info){
        Date date = new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(date);
        int month, day, year;
        year = Integer.parseInt(today.substring(0, 4));
        month = Integer.parseInt(today.substring(5, 7));
        day = Integer.parseInt(today.substring(8, 10));
        String ret;

        if(info.equals("Yesterday")){
            if(day == 1){
                if(month >= 1){
                    month -= 1;
                    day = getMonthDays(year, month);
                }
                else{
                    month = 12;
                    year -= 1;
                    day = 31;
                }
            }
            else{
                day -= 1;
            }
            ret = Format(year, month, day);
        }
        else if(info.equals("Tomorrow")){
            if(day == getMonthDays(year, month)){
                day = 1;
                if(month == 12){
                    year += 1;
                    month = 1;
                }
                else{
                    month += 1;
                }
            }
            else{
                day += 1;
            }
            ret = Format(year, month, day);
        }
        else if (info.equals("Today")) {
            ret = Format(year, month, day);
        }
        else{
            String[] buff = info.split(",");
            String[] buff2 = buff[1].split(" ");
            day = Integer.parseInt(buff2[1]);
            switch(buff2[2]){
                case "Jan": month = 1; break;
                case "Feb": month = 2; break;
                case "Mar": month = 3; break;
                case "Apr": month = 4; break;
                case "May": month = 5; break;
                case "June": month = 6; break;
                case "July": month = 7; break;
                case "Aug": month = 8; break;
                case "Sept": month = 9; break;
                case "Oct": month = 10; break;
                case "Nov": month = 11; break;
                case "Dec": month = 12; break;

            }
            ret = Format(year, month, day);
        }
        return ret;
    }

}
