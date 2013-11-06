package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/**
 * Data model for CalendarEvent objects.
 */
public class CalendarEvents {

    /** Interface to define Listener for the CalendarEvents model. */
    public interface EventsChangeListener{
        /** @param events The CalendarEvents model */
        void onEventsChange(CalendarEvents events);
    }

    private final LinkedList<CalendarEvent> _events = new LinkedList<CalendarEvent>();
    private final List<CalendarEvent> _safeEvents = Collections.unmodifiableList(_events);
    private CalendarDBOpenHelper _dbOpener;

    private EventsChangeListener _eventsChangeListener;

    /**
     * Set the listener for this CalendarEvents model.
     * @param l The EventsChangeListener
     */
    public void setEventsChangeListener(EventsChangeListener l){ _eventsChangeListener = l; }

    public void setUpDatabase(Context p_context){
        _dbOpener = new CalendarDBOpenHelper(p_context, CalendarDBOpenHelper.DATABASE_NAME, null, CalendarDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

    public boolean isEmpty(){ return _events.size() == 0; }

    private void loadDatabase(SQLiteDatabase p_db){
        String[] result_columns = new String[] { CalendarDBOpenHelper.KEY_EVENT_YEAR, CalendarDBOpenHelper.KEY_EVENT_MONTH, CalendarDBOpenHelper.KEY_EVENT_DAY,
                CalendarDBOpenHelper.KEY_EVENT_HOUR, CalendarDBOpenHelper.KEY_EVENT_MINUTE, CalendarDBOpenHelper.KEY_EVENT_NAME, CalendarDBOpenHelper.KEY_EVENT_LOCATION,
                CalendarDBOpenHelper.KEY_EVENT_REMINDER };

        Cursor cursor = p_db.query(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, result_columns, null, null, null, null, null);

        int yearIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_YEAR);
        int monthIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_MONTH);
        int dayIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_DAY);
        int hourIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_HOUR);
        int minuteIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_MINUTE);
        int nameIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_NAME);
        int locationIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_LOCATION);
        int reminderIndex = cursor.getColumnIndexOrThrow(CalendarDBOpenHelper.KEY_EVENT_REMINDER);

        while (cursor.moveToNext())
            _events.add(new CalendarEvent(cursor.getInt(yearIndex), cursor.getInt(dayIndex), cursor.getInt(monthIndex), cursor.getInt(hourIndex),
                    cursor.getInt(minuteIndex), cursor.getString(nameIndex), cursor.getString(locationIndex), cursor.getInt(reminderIndex)));

        notifyListener();
    }

    /**
     * Returns a list of all events in the model.
     * @return List of all CalendarEvents in the model.
     */
    public List<CalendarEvent> getEvents(){ return _safeEvents; }

    /**
     * Add a new event to the model.
     * @param p_year Event year
     * @param p_day Event day
     * @param p_month Event month
     * @param p_hour Event hour
     * @param p_minute Event minute
     * @param p_name Event name
     * @param p_location Event location
     * @param p_reminder Event reminder selection
     */
    public void addEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        _events.add(new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder));

        ContentValues newVals = new ContentValues();
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_YEAR, p_year);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_MONTH, p_month);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_DAY, p_day);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_HOUR, p_hour);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_MINUTE, p_minute);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_NAME, p_name);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_LOCATION, p_location);
        newVals.put(CalendarDBOpenHelper.KEY_EVENT_REMINDER, p_reminder);

        _dbOpener.getWritableDatabase().insert(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, null, newVals);
        notifyListener();
    }

    /**
     * Clear all events in the model.
     */
    public void clearEvents(){
        _events.clear();
        _dbOpener.getWritableDatabase().delete(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, null, null);
        notifyListener();
    }

    /**
     * Removes event at the specified position.
     * @param p_position Position to remove
     */
    public void removeEvent(int p_position){
        if (p_position < _events.size()){
            _events.remove(p_position);
            notifyListener();
        }
    }

    /**
     * Get a list of all events on a specified date, sorted by time.
     * @param p_year Desired year
     * @param p_day Desired day of month
     * @param p_month Desired month
     * @return List of applicable events
     */
    public List<CalendarEvent> getEventsOnDate(int p_year, int p_day, int p_month){
        ListIterator<CalendarEvent> iterator = _events.listIterator();
        LinkedList<CalendarEvent> returnList = new LinkedList<CalendarEvent>();
        int j;

        while (iterator.hasNext()){
            CalendarEvent event = iterator.next();
            if (event.matchDate(p_year, p_day, p_month)) {
                if (returnList.size() == 0)
                    returnList.add(event);
                else {
                    j = returnList.size();
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
        }

        return returnList;
    }

    private void notifyListener(){
        if (_eventsChangeListener != null)
            _eventsChangeListener.onEventsChange(this);
    }
}
