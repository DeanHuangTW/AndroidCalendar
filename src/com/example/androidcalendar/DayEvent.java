package com.example.androidcalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.Log;

public class DayEvent {
	private static String TAG = "DayEvent";
	
	public static final int PROJ_ID_INDEX = 0;
	public static final int PROJ_TITLE_INDEX = 1;
	public static final int PROJ_BEGIN_INDEX = 2;
	public static final int PROJ_END_INDEX = 3;
	public static final int PROJ_DESC_INDEX = 4;
	public static final int PROJ_DURATION_INDEX = 5;
	public static final int PROJ_ALLDAY_INDEX = 6;
	
	// About all filed, refer to CalendarContract.instance
	public static final String[] INSTANCE_PROJECTION = new String[] {
		    Instances.EVENT_ID,      // 0
		    Instances.TITLE,         // 1
		    Instances.BEGIN         // 2
	};
	
	// About all filed, refer to CalendarContract.Events
	public static final String[] EVENT_PROJECTION = new String[] {
			Events.CALENDAR_ID,     // 0
		    Events.TITLE,           // 1
		    Events.DTSTART,         // 2
		    Events.DTEND,			// 3
		    Events.DESCRIPTION,     // 4
		    Events.DURATION,		// 5
		    Events.ALL_DAY			// 6
	};
	
	private int mYear;
	private int mMonth;
	private int mDay;
	
	DayEvent(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
	}
	
	// According to eventID, query event's data
	public static Cursor queryEvntById(ContentResolver cr, int eventId) {
		Log.i(TAG, "queryEvntById. id :"+ eventId);
		Cursor cur = null;
		
		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
		cur = cr.query(uri, EVENT_PROJECTION, 
			    null, null, null);
		
		return cur;
	}

	// query today's event
	public Cursor queryTodayEvent(ContentResolver cr) {
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(mYear, mMonth, mDay, 0, 0);
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(mYear, mMonth, mDay, 23, 59);
		long endMillis = endTime.getTimeInMillis();
		
		Cursor cur = null;
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);
		
		// query event ID, title, begin time.
		cur = cr.query(builder.build(), INSTANCE_PROJECTION, 
		    null, null, Events.DTSTART + " ASC");
		
		return cur;
	}

}
