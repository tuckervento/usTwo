package com.mill_e.ustwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CalendarView;

public class CalendarHome extends Activity
{
	CalendarView calendar;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_home);
		calendar = (CalendarView)findViewById(R.id.root_calendar);
	}

}
