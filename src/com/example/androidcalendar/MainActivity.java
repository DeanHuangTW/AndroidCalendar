package com.example.androidcalendar;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity{

	private TextView mChooseDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
            	// show choose day
            	showDate(year, month  + 1, dayOfMonth);
            }
        });
	}
	
	private void showDate(int year, int month, int day) {
		mChooseDay.setText(String.valueOf(year) + "-" + 
				String.valueOf(month) + "-" + 
				String.valueOf(day));
	}

}
