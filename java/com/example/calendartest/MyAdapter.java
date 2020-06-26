package com.example.calendartest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, String>> mList = new ArrayList<>();

    public MyAdapter(Context context, List< Map<String, String> > list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.temp, null);
            viewHolder.eventName = (TextView) view.findViewById(R.id.eventname);
            viewHolder.eventDate = (TextView) view.findViewById(R.id.eventdate);
            viewHolder.eventTime = (TextView) view.findViewById(R.id.eventtime);
            viewHolder.mButton = (Button) view.findViewById(R.id.complete_btn);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.eventName.setText(mList.get(i).get("EventName"));
        viewHolder.eventDate.setText(mList.get(i).get("EventDate"));
        viewHolder.eventTime.setText(mList.get(i).get("EventTime"));
        viewHolder.mButton.setTag(i);
        viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.complete_btn:
                        Button btn = (Button) v.findViewById(R.id.complete_btn);
                        btn.setText("Completed!");
                        break;
                }
            }
        });
        return view;
    }

    class ViewHolder {
        TextView eventName;
        TextView eventDate;
        TextView eventTime;
        Button mButton;
    }

}