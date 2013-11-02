package com.mill_e.ustwo;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * This is the adapter for the ListView in CalendarEventListingFragment.
 */
public class CalendarEventListingArrayAdapter extends ArrayAdapter<CalendarEvent> {

    public CalendarEventListingArrayAdapter(Context context, int resource, List<CalendarEvent> objects) {
        super(context, resource, objects);
    }
}
