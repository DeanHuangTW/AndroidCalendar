package com.example.androidcalendar;

import com.example.androidcalendar.DayEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity{
	private String TAG = "MainActivity";
	
	
	private TextView mChooseDay;
	private TextView mNextMon;
	private TextView mPrevMon;
	private ListView mEventList;
	private CalendarView mCalendarView;	
	private SimpleAdapter adapter;
	
	// record selected date
	private int mYear;
	private int mMonth;
	private int mDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEventList = (ListView) findViewById(R.id.listView);
		
		mNextMon = (TextView)  findViewById(R.id.nextMonth);
		mPrevMon = (TextView)  findViewById(R.id.preMonth);
		mChooseDay = (TextView)  findViewById(R.id.chooseDay);
		// TextView default string is today
		Calendar mCalendar = Calendar.getInstance();
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH);
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		showDate(mYear, mMonth + 1, mDay);
		
		mCalendarView=(CalendarView) findViewById(R.id.calendarView);
		
		addItemsEvent();		
	}
	
	// Process items (TextView, ListView, CalendarView, ...) touch events
	private void addItemsEvent() {
		// click next month
		mNextMon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "click next month");
				Log.v(TAG, "   original date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				if (mMonth == 11) {
					// next year
					mMonth = 0;
					mYear++;
				} else {
					mMonth++;
				}
				Log.v(TAG, "   new date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				Calendar newDate = Calendar.getInstance();
				newDate.set(mYear, mMonth, mDay);
				mCalendarView.setDate(newDate.getTimeInMillis());
			}
		});
		
		// click previous month
		mPrevMon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "click previous month");
				Log.v(TAG, "   original date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				if (mMonth == 0) {
					// previous year
					mMonth = 11;
					mYear--;
				} else {
					mMonth--;
				}
				Log.v(TAG, "   new date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				Calendar newDate = Calendar.getInstance();
				newDate.set(mYear, mMonth, mDay);
				mCalendarView.setDate(newDate.getTimeInMillis());
			}
		});
		
		// select day change
		mCalendarView.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            	Log.v(TAG, "onSelectedDayChange");
            	// show choose day in textView
            	mYear = year;
            	mMonth = month;
            	mDay = dayOfMonth;
            	showDate(mYear, mMonth + 1, mDay);
            	// query events in this day
            	DayEvent dm = new DayEvent(year, month, dayOfMonth);
            	Cursor cur = dm.queryTodayEvent(getContentResolver());
            	showDayEvents(cur);
            }
        });
		
		// click a event on ListView
		mEventList.setOnItemClickListener(new OnItemClickListener() {  
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
            	Log.v(TAG, "ListView click event");
            	HashMap<String,String> data = (HashMap<String,String>)mEventList.getItemAtPosition(position);
            	int eventId = Integer.parseInt(data.get("ID"));
            	
            	Cursor cur = DayEvent.queryEvntById(getContentResolver(), eventId);
            	
            	while (cur.moveToNext()) {
	    		    long endVal = cur.getLong(DayEvent.PROJ_END_INDEX);
	    		    long beginVal = cur.getLong(DayEvent.PROJ_BEGIN_INDEX);
	    		    String title = cur.getString(DayEvent.PROJ_TITLE_INDEX);
	    		    String desc = cur.getString(DayEvent.PROJ_DESC_INDEX);
	    		    int allDay = cur.getInt(DayEvent.PROJ_ALLDAY_INDEX);
	
	    		    Calendar calendar = Calendar.getInstance();
	    		    calendar.setTimeInMillis(beginVal);
	    		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");
	
	    		    Log.i(TAG, title + " " + formatter.format(beginVal) + " - " + formatter.format(endVal));
	    		    Log.i(TAG, "Description:" + desc + "\nallDay: " + allDay);
            	}
            }
        }); 
	}
	
	// add events to ListView
	private void showDayEvents(Cursor cur) {
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		while (cur.moveToNext()) {
		    long eventID = cur.getLong(DayEvent.PROJ_ID_INDEX);
		    long beginVal = cur.getLong(DayEvent.PROJ_BEGIN_INDEX);
		    String title = cur.getString(DayEvent.PROJ_TITLE_INDEX);

		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");

		    HashMap<String,String> item = new HashMap<String,String>();
		    item.put("ID", String.valueOf(eventID)); // ID will not shown in ListView
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
