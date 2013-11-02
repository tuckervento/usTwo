package com.mill_e.ustwo;

import android.text.format.Time;

/**
 * Object representing a single calendar event.
 */
public class CalendarEvent extends TransmissionPayload
{
	private static final long serialVersionUID = -5228711660543513594L;
	private short _date;
	private short _year;
	private short _hour;
    private short _minute;
	private String _eventName;

    /**
     * Creates a new calendar event object.
     * @param p_time Event time
     * @param p_name Event name
     */
	public CalendarEvent(Time p_time, String p_name){
		_year = (short)p_time.year;
		_date = (short)p_time.yearDay;
		_hour = (short)p_time.hour;
        _minute = (short)p_time.minute;
		_eventName = p_name;
	}

    /**
     * Creates a new calendar event object.
     * @param p_year Event year
     * @param p_date Event date (yearday)
     * @param p_hour Event hour (24)
     * @param p_minute Event minute
     * @param p_name Event name
     */
    public CalendarEvent(short p_year, short p_date, short p_hour, short p_minute, String p_name){
        _hour = p_hour;
        _year = p_year;
        _minute = p_minute;
        _date = p_date;
        _eventName = p_name;
    }

    /**
     * Retrieve the event date.
     * @return Event date
     */
    public short getDate(){
        return _date;
    }

    /**
     * Retrieve the year of the event.
     * @return Event year
     */
    public short getYear(){ return _year; }

    /**
     * Retrieve the time of the event
     * @return Event time
     */
    public short getTime(){ return (short) (_hour*60+_minute); }

    /**
     * Returns the time of the event as a formatted string.
     * @return Formatted event time
     */
    public String getTimeAsString(){
        String end = "AM";
        int hour = 0;
        if (_hour > (short)11){
            end = "PM";
            if (_hour != (short)12)
                hour = _hour - 12;
            else
                hour = _hour;
        }
        else
            hour = _hour;

        return String.format("%02d:%02d %s", hour, (int)_minute, end);
    }

    /**
     * Retrieve the name of the event
     * @return Event name
     */
    public String getEventName(){ return this._eventName; }
}
