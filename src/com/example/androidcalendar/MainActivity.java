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
import android.widget.Button;
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
	private Button mAddEvent;
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
		mAddEvent = (Button) findViewById(R.id.addEvent);
		mNextMon = (TextView)  findViewById(R.id.nextMonth);
		mPrevMon = (TextView)  findViewById(R.id.preMonth);
		mChooseDay = (TextView)  findViewById(R.id.chooseDay);
		// TextView default string is today
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		showDate(mYear, mMonth + 1, mDay);
		
		mCalendarView=(CalendarView) findViewById(R.id.calendarView);
		
		addItemsEvent();		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "update info");
		// wait data update
		try {
		      Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
		       e.printStackTrace();
		}
		
		DayEvent dm = new DayEvent(mYear, mMonth, mDay);
    	Cursor cur = dm.queryTodayEvent(getContentResolver());
    	showDayEvents(cur);
	}
	
	// Process items (TextView, ListView, CalendarView, ...) touch events
	private void addItemsEvent() {
		// Add a event
		mAddEvent.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "Add event");
				addEvent();
			}
		});
		
		// click next month
		mNextMon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "click next month");
				Log.d(TAG, "   original date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				if (mMonth == 11) {
					// next year
					mMonth = 0;
					mYear++;
				} else {
					mMonth++;
				}
				Log.d(TAG, "   new date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				Calendar newDate = Calendar.getInstance();
				newDate.set(mYear, mMonth, mDay);
				mCalendarView.setDate(newDate.getTimeInMillis());
			}
		});
		
		// click previous month
		mPrevMon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "click previous month");
				Log.d(TAG, "   original date " + mYear + "/" + (mMonth+1) + "/" + mDay);
				if (mMonth == 0) {
					// previous year
					mMonth = 11;
					mYear--;
				} else {
					mMonth--;
				}
				Log.d(TAG, "   new date " + mYear + "/" + (mMonth+1) + "/" + mDay);
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
            	DayEvent dm = new DayEvent(mYear, mMonth, mDay);
            	Cursor cur = dm.queryTodayEvent(getContentResolver());
            	showDayEvents(cur);
            }
        });
		
		// When click a event on ListView, show calendar editor view
		mEventList.setOnItemClickListener(new OnItemClickListener() {  
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
            	Log.v(TAG, "ListView click event");
            	long endVal = 0, beginVal = 0;
            	
            	//Step 1. get event's data according to event ID
            	HashMap<String,String> data = (HashMap<String,String>)mEventList.getItemAtPosition(position);
            	int eventId = Integer.parseInt(data.get("ID"));
            	Cursor cur = DayEvent.queryEvntById(getContentResolver(), eventId);            	
            	while (cur.moveToNext()) { // Only one data
	    		    endVal = cur.getLong(DayEvent.PROJ_END_INDEX);
	    		    beginVal = cur.getLong(DayEvent.PROJ_BEGIN_INDEX);
            	}
            	
            	//Step 2. Write data to intent and open it.
            	Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            	//It will show 1970/1/1 if no set time
            	intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginVal);
            	intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endVal);
            	
            	startActivity(intent);
            }
        }); 
	}
	
	public void addEvent() {
		// set default time
		Calendar beginTime = Calendar.getInstance();
        beginTime.set(mYear, mMonth, mDay, beginTime.get(Calendar.HOUR), beginTime.get(Calendar.MINUTE));
        Calendar endTime = Calendar.getInstance();
        endTime.set(mYear, mMonth, mDay, endTime.get(Calendar.HOUR) + 1, endTime.get(Calendar.MINUTE));

        // 建立calendar event
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(Events.CONTENT_URI);
        // default value
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
        startActivity(intent);
        
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
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

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
				 
		//ListActivity設定adapter
		mEventList.setAdapter(adapter);
	}
	
	// show choose date
	private void showDate(int year, int month, int day) {
		mChooseDay.setText(String.valueOf(year) + "-" + 
				String.valueOf(month) + "-" + 
				String.valueOf(day));
	}

}
