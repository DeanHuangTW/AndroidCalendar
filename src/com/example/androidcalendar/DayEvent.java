package com.example.androidcalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Instances;
import android.util.Log;

public class DayEvent {
	private String TAG = "DayEvent";
	
	public static final String[] INSTANCE_PROJECTION = new String[] {
		    Instances.EVENT_ID,      // 0
		    Instances.BEGIN,         // 1
		    Instances.TITLE        // 2
	};
	
	private int mYear;
	private int mMonth;
	private int mDay;
	
	DayEvent(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}
	
	public Cursor queryTodayEvent(ContentResolver cr) {
		// query today event (00:00 ~ 23:59)
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(mYear, mMonth, mDay);
		  
		Cursor cur = null;
		double days = dateToJulian(beginTime);
		// 根据日期范围构造查询
		Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();
		ContentUris.appendId(builder, (long) days); // start day
		ContentUris.appendId(builder, (long) days); // end day

		// 提交查询
		cur = cr.query(builder.build(), 
		    INSTANCE_PROJECTION, 
		    null, 
		    null, 
		    null);
		/*
		while (cur.moveToNext()) {
		    long eventID = cur.getLong(PROJECTION_ID_INDEX);
		    long beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
		    String title = cur.getString(PROJECTION_TITLE_INDEX);

		    Log.i(TAG, "Event:  " + eventID + "  title: " + title); 
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		    Log.i(TAG, "    Date: " + formatter.format(calendar.getTime()));
		 }*/
		return cur;
	}

	public static double dateToJulian(Calendar date) {
	    int year = date.get(Calendar.YEAR);
	    int month = date.get(Calendar.MONTH)+1;
	    int day = date.get(Calendar.DAY_OF_MONTH);
	    int hour = date.get(Calendar.HOUR_OF_DAY);
	    int minute = date.get(Calendar.MINUTE);
	    int second = date.get(Calendar.SECOND);

	    double extra = (100.0 * year) + month - 190002.5;
	    return (367.0 * year) -
	    	(Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0)) + 
	    	Math.floor((275.0 * month) / 9.0) +  
	    	day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0) +
	    	1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
	  }
}
