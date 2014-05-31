package com.mill_e.ustwo.DataModel;

import java.util.Map;
/**
 * Object representing a single calendar event.
 */
public final class CalendarEvent extends TransmissionPayload
{
	private static final long serialVersionUID = -5228711660543513594L;
    public static final String JSON_TYPE = "CALENDAREVENT";
	private final int _day;
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
        this._day = p_day;
        this._eventName = p_name;
        this._eventLocation = p_eventLocation;
        this._reminder = p_reminder;
    }

    @Override
    public Map<String, String> getMap(){
        Map<String, String> map = super.getMap();
        map.put("Type", JSON_TYPE);
        map.put("Name", this._eventName);
        map.put("Location", this._eventLocation);
        map.put("Year", String.valueOf(this._year));
        map.put("Day", String.valueOf(this._day));
        map.put("Month", String.valueOf(this._month));
        map.put("Hour", String.valueOf(this._hour));
        map.put("Minute", String.valueOf(this._minute));
        map.put("Reminder", String.valueOf(this._reminder));
        return map;
    }

    /**
     * Retrieve the event day of month.
     * @return Event day of month
     */
    public int getDay(){ return this._day; }

    /**
     * Gets the month of the event.
     * @return Month of the event
     */
    public int getMonth(){ return this._month; }

    /**
     * Retrieve the year of the event.
     * @return Event year
     */
    public int getYear(){ return this._year; }

    /**
     * Gets the location of the event.
     * @return Event location
     */
    public String getLocation() { return this._eventLocation; }

    /**
     * Gets the reminder option of the event.
     * @return The selected reminder option
     */
    public int getReminder() { return this._reminder; }

    /**
     * Retrieve the time of the event as minutes since 00:00
     * @return Event time
     */
    public int getTime(){ return this._hour*60+this._minute; }

    /**
     * Gets the hour of the event.
     * @return Hour of the event
     */
    public int getHour(){ return this._hour; }

    /**
     * Gets the minute of the event.
     * @return Minute of the event
     */
    public int getMinute(){ return this._minute; }

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
