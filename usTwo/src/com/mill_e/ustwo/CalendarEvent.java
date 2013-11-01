package com.mill_e.ustwo;

import android.text.format.Time;

public class CalendarEvent extends TransmissionPayload
{
	private static final long serialVersionUID = -5228711660543513594L;
	private short date;
	private short year;
	private short time;
	private String eventName;

	public CalendarEvent(Time p_time, String p_name){
		year = (short)p_time.year;
		date = (short)p_time.yearDay;
		time = (short)(60*p_time.hour+p_time.minute);
		eventName = p_name;
	}
}
