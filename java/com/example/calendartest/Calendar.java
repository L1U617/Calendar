package com.example.calendartest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calendar extends AppCompatActivity {
    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_date;
    private TextView tv_week;
    private Button btn_complete;

    private MonthDateView monthDateView;
    private WeekDayView weekDayView;

    private Map< String, List< Map<String, String> > > eventDict;
    private ListView events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();

        eventDict = new HashMap<>();
        ArrayList<String> eventName = intent.getStringArrayListExtra("EventName");
        ArrayList<String> eventDate = intent.getStringArrayListExtra("EventDate");
        ArrayList<String> eventTime = intent.getStringArrayListExtra("EventTime");
        for(int i = 0; i < eventDate.size(); i++){
            List< Map<String, String> >list = new ArrayList<>();
            Map<String, String> map = new HashMap<String, String>();

            map.put("EventName", eventName.get(i));
            map.put("EventDate", eventDate.get(i));
            String[] buff = eventTime.get(i).split(",");
            map.put("EventTime", buff[1]);
            String date = DateUtils.Process(eventDate.get(i));
            System.out.println(eventDate.get(i)+" " + date);
            if(eventDict.containsKey(date)){
                list = eventDict.get(date);
                list.add(map);
                eventDict.put(date, list);
            }
            else{
                list.add(map);
                eventDict.put(date, list);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        monthDateView = (MonthDateView) findViewById(R.id.monthDateView);
        weekDayView = (WeekDayView) findViewById(R.id.weekDayView);
        tv_date = (TextView) findViewById(R.id.date_text);
        tv_week = (TextView) findViewById(R.id.week_text);

        events = (ListView) findViewById(R.id.tv_ToDoList);

        Typeface tf = ResourcesCompat.getFont(this, R.font.agencyb);
        weekDayView.setTf(tf);
        monthDateView.setTf(tf);
        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDaysHasThingList(eventDict);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate() {
                showToDoList();
            }
        });
        setOnlistener();
    }

    private void setOnlistener(){
        iv_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onRightClick();
            }
        });

    }

    private void showToDoList(){
        int day = monthDateView.getmSelDay();
        int month = monthDateView.getmSelMonth() + 1;
        int year = monthDateView.getmSelYear();
        String date = DateUtils.Format(year, month, day);
        //System.out.println(date);
        List< Map<String, String> > daylist = new ArrayList<>();

        if(eventDict.containsKey(date)){
            daylist = eventDict.get(date);

            MyAdapter adapter = new MyAdapter(this, daylist);
            events.setAdapter(adapter);
        }
        else{
            MyAdapter adapter = new MyAdapter(this, daylist);
            events.setAdapter(adapter);
        }
    }


}
