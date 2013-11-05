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
    private final String _eventLocation;
    private final int _reminder;

    /**
     * Creates a new calendar event object.
     * @param p_year Event year
     * @param p_day Event day
     * @param p_month Event month
     * @param p_hour Event hour (24)
     * @param p_minute Event minute
     * @param p_name Event name
     */
    public CalendarEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_eventLocation, int p_reminder){
        this._hour = p_hour;
        this._year = p_year;
        this._minute = p_minute;
        this._month = p_month;
        this._date = p_day;
        this._eventName = p_name;
        this._eventLocation = p_eventLocation;
        this._reminder = p_reminder;
    }

    /**
     * Retrieve the event date.
     * @return Event date
     */
    public int getDate(){
        return this._date;
    }

    /**
     * Retrieve the year of the event.
     * @return Event year
     */
    public int getYear(){ return this._year; }

    public String getLocation() { return this._eventLocation; }

    public int getReminder() { return this._reminder; }

    /**
     * Retrieve the time of the event as minutes since 00:00
     * @return Event time
     */
    public int getTime(){ return this._hour*60+this._minute; }

    /**
     * Check to see if the specified date matches the event.
     * @param p_year Event year
     * @param p_day Event day
     * @param p_month Event month
     * @return Boolean indicating a match
     */
    public boolean matchDate(int p_year, int p_day, int p_month){ return (this._year == p_year) && (this._date == p_day) && (this._month == p_month); }

    /**
     * Returns the time of the event as a formatted string.
     * @return Formatted event time
     */
    public String getTimeAsString(){
        String end = "AM";
        int hour;
        if (this._hour > 11){
            end = "PM";
            if (this._hour != 12)
                hour = this._hour - 12;
            else
                hour = this._hour;
        }
        else
            hour = this._hour;

        return String.format("%02d:%02d %s", hour, this._minute, end);
    }

    /**
     * Retrieve the name of the event
     * @return Event name
     */
    public String getEventName(){ return this._eventName; }
}
