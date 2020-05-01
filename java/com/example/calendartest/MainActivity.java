package com.example.calendartest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_date;
    private TextView tv_week;
    private TextView tv_today;
    private MonthDateView monthDateView;

    private Map< Integer, List< Map<String, Object> > > eventDict;
    private ListView events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        eventDict = new HashMap<>();
        List< Map<String, Object> >list = new ArrayList<Map<String, Object>>();
        ArrayList<String> cname = new ArrayList<String>();
        ArrayList<String> cteachers = new ArrayList<String>();
        cname.add("c1");        cteachers.add("t1");
        cname.add("c2");        cteachers.add("t2");
        cname.add("c3");        cteachers.add("t3");
        cname.add("c4");        cteachers.add("t4");
        for (int i = 0; i < 4; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("CourseName", cname.get(i));
            System.out.println(cteachers.get(i));
            map.put("Teachers", cteachers.get(i));
            list.add(map);
        }
        System.out.println(list);
        eventDict.put(4, list);
        System.out.println(eventDict);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        monthDateView = (MonthDateView) findViewById(R.id.monthDateView);
        tv_date = (TextView) findViewById(R.id.date_text);
        tv_week = (TextView) findViewById(R.id.week_text);
        tv_today = (TextView) findViewById(R.id.tv_today);

        events = (ListView) findViewById(R.id.tv_ToDoList);

        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDaysHasThingList(eventDict);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
                @Override
            public void onClickOnDate() {
                Toast.makeText(getApplication(), "点击了：" + monthDateView.getmSelDay(), Toast.LENGTH_SHORT).show();
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

        tv_today.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.setTodayToView();
            }
        });
    }

    private void showToDoList(){
        int day = monthDateView.getmSelDay();
        System.out.println(day);
        List< Map<String, Object> > daylist = new ArrayList<>();

        if(eventDict.containsKey(day)){
            daylist = eventDict.get(day);

            SimpleAdapter adapter = new SimpleAdapter(this, daylist, R.layout.temp, new String[]{"CourseName","Teachers"},
                    new int[]{R.id.coursename, R.id.teachers} );
            events.setAdapter(adapter);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, daylist, R.layout.temp, new String[]{"CourseName","Teachers"},
                new int[]{R.id.coursename, R.id.teachers} );
        events.setAdapter(adapter);
    }
}
