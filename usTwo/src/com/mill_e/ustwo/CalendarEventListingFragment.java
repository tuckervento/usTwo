package com.mill_e.ustwo;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This is fragment for displaying a list of calendar events.
 */
public class CalendarEventListingFragment extends ListFragment implements OnClickListener{

    private short year;
    private short month;
    private short day;
    private ArrayList<CalendarEvent> calendarEventArrayList;

    public CalendarEventListingFragment(short p_year, short p_month, short p_day){
        this.year = p_year;
        this.month = p_month;
        this.day = p_day;
        calendarEventArrayList = new ArrayList<CalendarEvent>();
    }

    private void simulateCalendarEvents(){
        for (short i = 0; i < 24; i++)
            calendarEventArrayList.add(new CalendarEvent(year, (short) (month+day), i, (short) 0, "Test"));
        refreshMessages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_calendar_event_listing, container, false);
        Button b1 = (Button) v.findViewById(R.id.button_next_day);
        Button b2 = (Button) v.findViewById(R.id.button_previous_day);
        Button b3 = (Button) v.findViewById(R.id.button_create_event);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        TextView textView = (TextView) v.findViewById(R.id.textView_listing_day);
        String mon;
        switch ((int)month){
            case 1:
                mon = "Jan";
                break;
            case 2:
                mon = "Feb";
                break;
            case 3:
                mon = "Mar";
                break;
            case 4:
                mon = "Apr";
                break;
            case 5:
                mon = "May";
                break;
            case 6:
                mon = "Jun";
                break;
            case 7:
                mon = "Jul";
                break;
            case 8:
                mon = "Aug";
                break;
            case 9:
                mon = "Sep";
                break;
            case 10:
                mon = "Oct";
                break;
            case 11:
                mon = "Nov";
                break;
            case 12:
                mon = "Dec";
                break;
            default:
                mon = "Error";
                break;
        } //picks month string
        textView.setText(String.format("%s %d", mon, (int)day));
        return v;
    }

    /**
     * Refreshes the ListView.
     */
    private void refreshMessages(){
        ListView listView = super.getListView();
        CalendarEventArrayAdapter adapter = (CalendarEventArrayAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void goToNextDay(){

    }

    private void goToPreviousDay(){

    }

    private void createEvent(){

    }

    public void onViewCreated(View view, Bundle bundle){
        super.setListAdapter(new CalendarEventArrayAdapter(view.getContext(), R.layout.calendar_event_layout, calendarEventArrayList));
        if (!calendarEventArrayList.isEmpty())
            refreshMessages();
        simulateCalendarEvents();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_next_day:
                goToNextDay();
                break;
            case R.id.button_previous_day:
                goToPreviousDay();
                break;
            case R.id.button_create_event:
                createEvent();
                break;
        }
    }
}