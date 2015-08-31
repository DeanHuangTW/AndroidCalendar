package com.example.androidcalendar;

import com.example.androidcalendar.DayEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity{
	private String TAG = "MainActivity";
	
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_BEGIN_INDEX = 1;
	private static final int PROJECTION_TITLE_INDEX = 2;
	
	private TextView mChooseDay;
	private ListView mEventList;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEventList = (ListView) findViewById(R.id.listView);
		
		
		mChooseDay = (TextView)  findViewById(R.id.chooseDay);
		// TextView default string is today
		Calendar mCalendar = Calendar.getInstance();
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		showDate(year, month, day);
		
		// click date
		CalendarView calendarView=(CalendarView) findViewById(R.id.calendarView);
		calendarView.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            	// show choose day in textView
            	showDate(year, month  + 1, dayOfMonth);
            	// query events in this day
            	DayEvent dm = new DayEvent(year, month, dayOfMonth);
            	Cursor cur = dm.queryTodayEvent(getContentResolver());
            	showEvent(cur);
            }
        });
	}
	
	// add events to ListView
	private void showEvent(Cursor cur) {
		while (cur.moveToNext()) {
		    long eventID = cur.getLong(PROJECTION_ID_INDEX);
		    long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
		    String title = cur.getString(PROJECTION_TITLE_INDEX);

		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		    
		    HashMap<String,String> item = new HashMap<String,String>();
		    item.put("Title", title);
		    item.put("startTime", formatter.format(calendar.getTime()));
		    list.add(item);
		}
		adapter = new SimpleAdapter(this, 
			list,
			android.R.layout.simple_list_item_2,
			new String[] { "Title","startTime" },
			new int[] {android.R.id.text1, android.R.id.text2});
				 
		//ListActivity³]©wadapter
		mEventList.setAdapter(adapter);
	}
	
	// show choose date
	private void showDate(int year, int month, int day) {
		mChooseDay.setText(String.valueOf(year) + "-" + 
				String.valueOf(month) + "-" + 
				String.valueOf(day));
	}

}
