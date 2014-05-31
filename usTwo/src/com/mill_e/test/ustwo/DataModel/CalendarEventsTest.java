package com.mill_e.test.ustwo.DataModel;

import android.test.InstrumentationTestCase;

import com.mill_e.ustwo.DataModel.CalendarEvent;
import com.mill_e.ustwo.DataModel.CalendarEvents;

public class CalendarEventsTest extends InstrumentationTestCase {

    private CalendarEvents _events;

    public void setUp() throws Exception {
        super.setUp();

        _events = new CalendarEvents();
        _events.setUpDatabase(getInstrumentation().getTargetContext());
    }

    public void tearDown() throws Exception {
        _events.clearModel();
        _events.closeDatabase();
        _events = null;
    }

    public void testIsEmpty() throws Exception {
        assertTrue(_events.isEmpty());

        _events.addEvent(new CalendarEvent(0, 0, 0, 0, 0, "Empty Test", "Location", 0));

        assertFalse(_events.isEmpty());
    }

    public void testClearModel() throws Exception {
        _events.addEvent(new CalendarEvent(0, 0, 0, 0, 0, "Clear Test", "Location", 0));

        assertFalse(_events.isEmpty());

        _events.clearModel();

        assertTrue(_events.isEmpty());
    }

    public void testEditEvent() throws Exception {
        CalendarEvent event = new CalendarEvent(0, 0, 0, 0, 0, "Edit Test", "Loc", 0);
        _events.addEvent(event);
        CalendarEvent event2 = new CalendarEvent(1, 1, 1, 1, 1, "Edit Test 2", "Loc", 0);
        _events.addEvent(event2);

        CalendarEvent event3 = (CalendarEvent) new CalendarEvent(2, 2, 2, 2, 2, "Edit Test 3", "Loc", 0).setPayloadInfo(event.getTimeStamp(), event.getSender());
        _events.editEvent(event3.getTimeStamp(), event3.getYear(), event3.getDay(), event3.getMonth(), event3.getHour(), event3.getMinute(), event3.getEventName(), event3.getLocation(), event3.getReminder());

        assertTrue(_events.containsEvent(event3));
        assertTrue(_events.containsEvent(event2));
        assertFalse(_events.containsEvent(event));
    }

    public void testRemoveEvent() throws Exception {
        CalendarEvent event = new CalendarEvent(0, 0, 0, 0, 0, "Remove Test", "Loc", 0);
        _events.addEvent(event);
        CalendarEvent event2 = new CalendarEvent(1, 1, 1, 1, 1, "Remove Test 2", "Loc", 0);
        _events.addEvent(event2);

        assertTrue(_events.containsEvent(event));
        assertTrue(_events.containsEvent(event2));

        _events.removeEvent(event.getTimeStamp());

        assertFalse(_events.containsEvent(event));
        assertTrue(_events.containsEvent(event2));
    }

    public void testContainsEvent() throws Exception {
        CalendarEvent event = new CalendarEvent(0, 0, 0, 0, 0, "Contains Test", "Loc", 0);
        _events.addEvent(event);

        assertTrue(_events.containsEvent(event));

        CalendarEvent event2 = new CalendarEvent(1, 1, 1, 1, 1, "Contains Test 2", "Loc", 0);
        _events.addEvent(event);
        _events.addEvent(event);

        assertFalse(_events.containsEvent(event2));
    }

    public void testGetEventsOnDate() throws Exception {

    }
}
