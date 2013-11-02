package com.mill_e.ustwo;

import android.text.format.Time;

/**
 * Object representing a single calendar event.
 */
public class CalendarEvent extends TransmissionPayload
{
	private static final long serialVersionUID = -5228711660543513594L;
	private short date;
	private short year;
	private short time;
	private String eventName;

    /**
     * Creates a new calendar event object.
     * @param p_time Event time
     * @param p_name Event name
     */
	public CalendarEvent(Time p_time, String p_name){
		year = (short)p_time.year;
		date = (short)p_time.yearDay;
		time = (short)(60*p_time.hour+p_time.minute);
		eventName = p_name;
	}

    /**
     * Retrieve the event date.
     * @return Event date
     */
    public short getDate(){
        return this.date;
    }

    /**
     * Retrieve the year of the event.
     * @return Event year
     */
    public short getYear(){ return this.year; }

    /**
     * Retrieve the time of the event
     * @return Event time
     */
    public short getTime(){ return this.time; }

    /**
     * Retrieve the name of the event
     * @return Event name
     */
    public String getEventName(){ return this.eventName; }
}
