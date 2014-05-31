package com.mill_e.ustwo.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.LongSparseArray;

import com.mill_e.ustwo.DataModel.Helpers.CalendarDBOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * Data model for CalendarEvent objects.
 */
public class CalendarEvents extends UsTwoDataModel {

    private final HashMap<String, List<CalendarEvent>> _events = new HashMap<String, List<CalendarEvent>>();
    private final LongSparseArray<CalendarEvent> _eventsByStamp = new LongSparseArray<CalendarEvent>();
    private final Map<String, List<CalendarEvent>> _safeEvents = Collections.unmodifiableMap(_events);
    private CalendarDBOpenHelper _dbOpener;

    private static boolean FINISHED_LOADING = false;

    private DataModelChangeListener _eventsChangeListener;

    //region UsTwoDataModel
    @Override
    public void setDataModelChangeListener(DataModelChangeListener l){ _eventsChangeListener = l; }

    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new CalendarDBOpenHelper(p_context);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

    /**
     * Returns a boolean to indicate whether the data model has finished loading from the SQLite database.
     * @return Boolean indicating whether the data model has finished loading from the SQLite database
     */
    public boolean isFinishedLoading(){ return FINISHED_LOADING; }

    @Override
    public boolean isEmpty(){ return _eventsByStamp.size() == 0; }

    @Override
    public void clearModel(){
        _events.clear();
        _eventsByStamp.clear();
        _dbOpener.getWritableDatabase().delete(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, null, null);
        _dbOpener.close();
        notifyListener();
    }

    @Override
    public void closeDatabase(){ _dbOpener.close(); }

    private void loadDatabase(SQLiteDatabase p_db){
        String[] result_columns = new String[] { CalendarDBOpenHelper.KEY_TIMESTAMP, CalendarDBOpenHelper.KEY_SENDER, CalendarDBOpenHelper.KEY_EVENT_NAME,
                CalendarDBOpenHelper.KEY_EVENT_LOCATION, CalendarDBOpenHelper.KEY_EVENT_YEAR, CalendarDBOpenHelper.KEY_EVENT_MONTH, CalendarDBOpenHelper.KEY_EVENT_DAY,
                CalendarDBOpenHelper.KEY_EVENT_HOUR, CalendarDBOpenHelper.KEY_EVENT_MINUTE, CalendarDBOpenHelper.KEY_EVENT_REMINDER };

        Cursor cursor = p_db.query(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, result_columns, null, null, null, null, null);

        int timestampIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_TIMESTAMP);
        int senderIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_SENDER);
        int nameIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_NAME);
        int locationIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_LOCATION);
        int yearIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_YEAR);
        int monthIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_MONTH);
        int dayIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_DAY);
        int hourIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_HOUR);
        int minuteIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_MINUTE);
        int reminderIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_REMINDER);

        while (cursor.moveToNext())
           silentAddEvent((CalendarEvent) new CalendarEvent(cursor.getInt(yearIndex), cursor.getInt(dayIndex), cursor.getInt(monthIndex), cursor.getInt(hourIndex),
                   cursor.getInt(minuteIndex), cursor.getString(nameIndex), cursor.getString(locationIndex), cursor.getInt(reminderIndex)).setPayloadInfo(cursor.getLong(timestampIndex), cursor.getString(senderIndex)));
        cursor.close();
        notifyListener();
        FINISHED_LOADING = true;
    }

    private void notifyListener(){
        if (_eventsChangeListener != null)
            _eventsChangeListener.onDataModelChange();
    }
    //endregion

    /**
     * Returns a Dictionary of all events in the model.
     * @return Dictionary of all CalendarEvents in the model.
     */
    public Map<String, List<CalendarEvent>> getEvents(){ return _safeEvents; }

    /**
     * Adds a CalendarEvent object to the data model.
     * @param p_calendarEvent The CalendarEvent to add
     * @return The event added
     */
    public CalendarEvent addEvent(CalendarEvent p_calendarEvent) {
        String stringifiedDate = String.valueOf(p_calendarEvent.getDay()) + String.valueOf(p_calendarEvent.getMonth()) + String.valueOf(p_calendarEvent.getYear());

        if (_events.containsKey(stringifiedDate)) {
            if (dayContainsEvent(_events.get(stringifiedDate), p_calendarEvent)) { return p_calendarEvent; }
            _events.get(stringifiedDate).add(p_calendarEvent);
            _eventsByStamp.put(p_calendarEvent.getTimeStamp(), p_calendarEvent);
        }else{
            _events.put(stringifiedDate, new ArrayList<CalendarEvent>());
            _events.get(stringifiedDate).add(p_calendarEvent);
            _eventsByStamp.put(p_calendarEvent.getTimeStamp(), p_calendarEvent);
        }

        ContentValues newVals = new ContentValues();
        newVals.put(CalendarDBOpenHelper.KEY_TIMESTAMP, p_calendarEvent.getTimeStamp());
        newVals.put(CalendarDBOpenHelper.KEY_SENDER, p_calendarEvent.getSender());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_NAME, p_calendarEvent.getEventName());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_LOCATION, p_calendarEvent.getLocation());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_YEAR, p_calendarEvent.getYear());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_MONTH, p_calendarEvent.getMonth());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_DAY, p_calendarEvent.getDay());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_HOUR, p_calendarEvent.getHour());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_MINUTE, p_calendarEvent.getMinute());
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_REMINDER, p_calendarEvent.getReminder());

        _dbOpener.getWritableDatabase().insert(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, null, newVals);
        _dbOpener.close();
        notifyListener();

        return p_calendarEvent;
    }

    /**
     * Edits an existing calendar event.
     * @param p_timestamp The timestamp of the existing event
     * @param p_year The year of the event
     * @param p_day The day of the event
     * @param p_month The month of the event
     * @param p_hour The hour of the event
     * @param p_minute The minute of the event
     * @param p_name The name of the event
     * @param p_location The location of the event
     * @param p_reminder The reminder selection for the event
     * @return The event edited, or null if the edit failed
     */
    public CalendarEvent editEvent(long p_timestamp, int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        String sender = "";
        CalendarEvent event = new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder);
        String date = stringifyDate(event);

        if (_eventsByStamp.indexOfKey(p_timestamp) >= 0) {
            _events.get(date).remove(getEventIndexOnDayList(date, p_timestamp));
        } else { return null; }

        _dbOpener.getWritableDatabase().delete(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, CalendarDBOpenHelper.KEY_TIMESTAMP + " = " + String.valueOf(p_timestamp), null);
        _dbOpener.close();

        return addEvent((CalendarEvent) event.setPayloadInfo(p_timestamp, sender));
    }

    /**
     * Removes event at the specified position.
     * @param p_timestamp The timestamp of the event to remove
     */
    public void removeEvent(long p_timestamp){
        if (_eventsByStamp.indexOfKey(p_timestamp) >= 0){
            String date = stringifyDate(_eventsByStamp.get(p_timestamp));
            _events.get(date).remove(getEventIndexOnDayList(date, p_timestamp));
            _eventsByStamp.remove(p_timestamp);
        }
    }

    /**
     * Checks if the provided CalendarEvent exists within the data model.
     * @param p_event The CalendarEvent to search for
     * @return Boolean indicator
     */
    public boolean containsEvent(CalendarEvent p_event){
        return _eventsByStamp.indexOfKey(p_event.getTimeStamp()) >= 0;
    }

    /**
     * Get a list of all events on a specified date, sorted by time.
     * @param p_year Desired year
     * @param p_day Desired day of month
     * @param p_month Desired month
     * @return List of applicable events
     */
    public List<CalendarEvent> getEventsOnDate(int p_year, int p_day, int p_month){
        String date = String.valueOf(p_day) + String.valueOf(p_month) + String.valueOf(p_year);
        LinkedList<CalendarEvent> returnList = new LinkedList<CalendarEvent>();

        if (!_events.containsKey(date)) { return returnList; }
        List<CalendarEvent> dayList = _events.get(date);

        for (CalendarEvent event : dayList) {
            if (returnList.size() == 0)
                returnList.add(event);
            else {
                int j = returnList.size();
                for (int i = 0; i < j; i++) {
                    if (returnList.get(i).getTime() > event.getTime()) {
                        returnList.add(i, event);
                        break;
                    }
                    else if (i+1 == j)
                        returnList.add(event);
                }

            }
        }

        return returnList;
    }

    //region Private

    private void silentAddEvent(CalendarEvent p_calendarEvent) {
        String stringifiedDate = String.valueOf(p_calendarEvent.getDay()) + String.valueOf(p_calendarEvent.getMonth()) + String.valueOf(p_calendarEvent.getYear());

        if (_events.containsKey(stringifiedDate)) {
            if (dayContainsEvent(_events.get(stringifiedDate), p_calendarEvent)) {
                return;
            }
            _events.get(stringifiedDate).add(p_calendarEvent);
            _eventsByStamp.put(p_calendarEvent.getTimeStamp(), p_calendarEvent);
        } else {
            _events.put(stringifiedDate, new ArrayList<CalendarEvent>());
            _events.get(stringifiedDate).add(p_calendarEvent);
            _eventsByStamp.put(p_calendarEvent.getTimeStamp(), p_calendarEvent);
        }
    }

    private int getEventIndexOnDayList(CalendarEvent p_event) {
        List<CalendarEvent> dayList = _events.get(stringifyDate(p_event));
        for (int i = 0; i < dayList.size(); i++) {
            if (dayList.get(i).getTimeStamp() == p_event.getTimeStamp()) { return i; }
        }

        return -1;
    }

    private int getEventIndexOnDayList(String date, long p_timestamp) {
        List<CalendarEvent> dayList = _events.get(date);
        for (int i = 0; i < dayList.size(); i++) {
            if (dayList.get(i).getTimeStamp() == p_timestamp) { return i; }
        }

        return -1;
    }

    private String stringifyDate(CalendarEvent p_event) {
        return String.valueOf(p_event.getDay()) + String.valueOf(p_event.getMonth()) + String.valueOf(p_event.getYear());
    }

    private boolean dayContainsEvent(List<CalendarEvent> p_dayList, CalendarEvent p_event) {
        boolean result = false;
        long timestamp = p_event.getTimeStamp();

        for (CalendarEvent event : p_dayList){
            if (event.getTimeStamp() == timestamp) {
                result = true;
                break;
            }
        }

        return result;
    }

    //endregion
}
