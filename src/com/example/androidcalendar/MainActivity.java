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
import android.view.View;
import android.view.View.OnClickListener;
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
	
	// Add items (TextView, CalendarView, ...) events
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
					// next year
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
	}
	
	// add events to ListView
	private void showDayEvents(Cursor cur) {
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		while (cur.moveToNext()) {
		    long eventID = cur.getLong(PROJECTION_ID_INDEX);
		    long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
		    String title = cur.getString(PROJECTION_TITLE_INDEX);

		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");

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
