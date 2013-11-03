package com.mill_e.ustwo;

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

    private EventsChangeListener _eventsChangeListener;

    /**
     * Set the listener for this CalendarEvents model.
     * @param l The EventsChangeListener
     */
    public void setEventsChangeListener(EventsChangeListener l){ _eventsChangeListener = l; }

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
     */
    public void addEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name){
        _events.add(new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name));
        notifyListener();
    }

    /**
     * Clear all events in the model.
     */
    public void clearEvents(){
        _events.clear();
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

        while (iterator.hasNext()){
            CalendarEvent event = iterator.next();
            if (event.matchDate(p_year, p_day, p_month)){
                if (returnList.size() == 0)
                    returnList.add(event);
                for (int i = 0; i < returnList.size(); i++){
                    if (returnList.get(i).getTime() > event.getTime())
                        returnList.add(i, event);
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
