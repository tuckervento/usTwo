package com.mill_e.ustwo;

/**
 * Object representing a single calendar event.
 */
public final class CalendarEvent extends TransmissionPayload
{
	private static final long serialVersionUID = -5228711660543513594L;
	private final int _date;
    private final int _month;
	private final int _year;
	private final int _hour;
    private final int _minute;
	private final String _eventName;

    /**
     * Creates a new calendar event object.
     * @param p_year Event year
     * @param p_day Event day
     * @param p_month Event month
     * @param p_hour Event hour (24)
     * @param p_minute Event minute
     * @param p_name Event name
     */
    public CalendarEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name){
        _hour = p_hour;
        _year = p_year;
        _minute = p_minute;
        _month = p_month;
        _date = p_day;
        _eventName = p_name;
    }

    /**
     * Retrieve the event date.
     * @return Event date
     */
    public int getDate(){
        return _date;
    }

    /**
     * Retrieve the year of the event.
     * @return Event year
     */
    public int getYear(){ return _year; }

    /**
     * Retrieve the time of the event as minutes since 00:00
     * @return Event time
     */
    public int getTime(){ return _hour*60+_minute; }

    /**
     * Check to see if the specified date matches the event.
     * @param p_year Event year
     * @param p_day Event day
     * @param p_month Event month
     * @return Boolean indicating a match
     */
    public boolean matchDate(int p_year, int p_day, int p_month){ return (_year == p_year) && (_date == p_day) && (_month == p_month); }

    /**
     * Returns the time of the event as a formatted string.
     * @return Formatted event time
     */
    public String getTimeAsString(){
        String end = "AM";
        int hour;
        if (_hour > 11){
            end = "PM";
            if (_hour != 12)
                hour = _hour - 12;
            else
                hour = _hour;
        }
        else
            hour = _hour;

        return String.format("%02d:%02d %s", hour, _minute, end);
    }

    /**
     * Retrieve the name of the event
     * @return Event name
     */
    public String getEventName(){ return this._eventName; }
}
