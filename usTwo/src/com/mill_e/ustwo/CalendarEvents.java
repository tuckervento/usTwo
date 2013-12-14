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
public class CalendarEvents extends UsTwoDataModel{

    private final LinkedList<CalendarEvent> _events = new LinkedList<CalendarEvent>();
    private final List<CalendarEvent> _safeEvents = Collections.unmodifiableList(_events);
    private CalendarDBOpenHelper _dbOpener;

    private static boolean FINISHED_LOADING = false;

    private DataModelChangeListener _eventsChangeListener;

    //region UsTwoDataModel
    @Override
    public void setDataModelChangeListener(DataModelChangeListener l){ _eventsChangeListener = l; }

    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new CalendarDBOpenHelper(p_context, CalendarDBOpenHelper.DATABASE_NAME, null, CalendarDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

    /**
     * Returns a boolean to indicate whether the data model has finished loading from the SQLite database.
     * @return Boolean indicating whether the data model has finished loading from the SQLite database
     */
    public boolean isFinishedLoading(){ return this.FINISHED_LOADING; }

    @Override
    public boolean isEmpty(){ return _events.size() == 0; }

    @Override
    public void clearModel(){
        _events.clear();
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
            _events.add((CalendarEvent) new CalendarEvent(cursor.getInt(yearIndex), cursor.getInt(dayIndex), cursor.getInt(monthIndex), cursor.getInt(hourIndex),
                    cursor.getInt(minuteIndex), cursor.getString(nameIndex), cursor.getString(locationIndex), cursor.getInt(reminderIndex)).setPayloadInfo(cursor.getLong(timestampIndex), cursor.getString(senderIndex)));
        cursor.close();
        notifyListener();
        FINISHED_LOADING = true;
    }

    private void notifyListener(){
        if (_eventsChangeListener != null)
            _eventsChangeListener.onDataModelChange(this);
    }
    //endregion

    /**
     * Returns a list of all events in the model.
     * @return List of all CalendarEvents in the model.
     */
    public List<CalendarEvent> getEvents(){ return _safeEvents; }

    /**
     * Adds a CalendarEvent object to the data model.
     * @param p_calendarEvent The CalendarEvent to add
     * @return The event added
     */
    public CalendarEvent addEvent(CalendarEvent p_calendarEvent) {
        if (_events.contains(p_calendarEvent))
            return p_calendarEvent;

        _events.add(p_calendarEvent);

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
     * @param p_timeStamp The timestamp of the existing event
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
    public CalendarEvent editEvent(long p_timeStamp, int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        boolean found = false;
        String sender = "";

        for (int i = 0; i < _events.size(); i++)
            if (_events.get(i).getTimeStamp() == p_timeStamp){
                sender = _events.get(i).getSender();
                found = true;
                _events.remove(i);
                break;
            }

        if (!found)
            return null;

        _dbOpener.getWritableDatabase().delete(CalendarDBOpenHelper.EVENTS_DATABASE_TABLE, CalendarDBOpenHelper.KEY_TIMESTAMP + " = " + String.valueOf(p_timeStamp), null);
        _dbOpener.close();

        return addEvent((CalendarEvent) new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder).setPayloadInfo(p_timeStamp, sender));
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
     * Checks if the provided CalendarEvent exists within the data model.
     * @param p_event The CalendarEvent to search for
     * @return Boolean indicator
     */
    public boolean containsEvent(CalendarEvent p_event){ return _events.contains(p_event); }

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
}
