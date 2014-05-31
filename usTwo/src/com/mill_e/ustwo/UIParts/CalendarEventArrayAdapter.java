package com.mill_e.ustwo.UIParts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mill_e.ustwo.DataModel.CalendarEvent;
import com.mill_e.ustwo.R;

import java.util.List;

/**
 * This is the adapter for the ListView in CalendarEventListingFragment.
 */
public class CalendarEventArrayAdapter extends ArrayAdapter<CalendarEvent> {

    private final List<CalendarEvent> _events;

    public CalendarEventArrayAdapter(Context context, List<CalendarEvent> objects) {
        super(context, R.layout.calendar_event_layout, objects);
        this._events = objects;
    }

    private static class ViewHolder{
        public TextView eventName;
        public TextView eventTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        final CalendarEvent event = _events.get(position);
        if (convertView == null) { convertView = inflater.inflate(R.layout.calendar_event_layout, parent, false); }
        holder = new ViewHolder();
        holder.eventName = (TextView) convertView.findViewById(R.id.textView_event_name);
        holder.eventTime = (TextView) convertView.findViewById(R.id.textView_event_time);
        convertView.setTag(holder);

        if (holder.eventName.getText().equals("") && event != null)
            holder.eventName.setText(event.getEventName());

        if (holder.eventTime.getText().toString().equals(context.getString(R.string.textView_blank_time)) && event != null)
            holder.eventTime.setText(event.getTimeAsString());

        return convertView;
    }

    /**
     * Gets the event for this adapter's day at the specified position.
     * @param p_position The position to check
     * @return The CalendarEvent at the position provided
     */
    public CalendarEvent getEvent(int p_position){ return _events.get(p_position); }
}
