package com.mill_e.ustwo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This is the adapter for the ListView in CalendarEventListingFragment.
 */
public class CalendarEventArrayAdapter extends ArrayAdapter<CalendarEvent> {

    private final Context _context;
    private List<CalendarEvent> _events;

    public CalendarEventArrayAdapter(Context context, int resource, List<CalendarEvent> objects) {
        super(context, resource, objects);
        this._context = context;
        this._events = objects;
    }

    private static class ViewHolder{
        public TextView eventName;
        public TextView eventTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        final CalendarEvent event = _events.get(position);

        convertView = inflater.inflate(R.layout.calendar_event_layout, null);
        holder = new ViewHolder();
        holder.eventName = (TextView) convertView.findViewById(R.id.textView_event_name);
        holder.eventTime = (TextView) convertView.findViewById(R.id.textView_event_time);
        convertView.setTag(holder);

        if (holder.eventName.getText().equals("") && event != null)
            holder.eventName.setText(event.getEventName());

        if (holder.eventTime.getText().toString().equals(_context.getString(R.string.textView_blank_time)) && event != null)
            holder.eventTime.setText(event.getTimeAsString());

        return convertView;
    }
}
