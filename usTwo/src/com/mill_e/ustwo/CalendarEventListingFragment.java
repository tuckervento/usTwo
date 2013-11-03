package com.mill_e.ustwo;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * This is fragment for displaying a list of calendar events.
 */
public class CalendarEventListingFragment extends ListFragment implements OnClickListener{

    private final int _year;
    private final int _month;
    private final int _day;
    private final CalendarEvents _events;


    public CalendarEventListingFragment(int p_year, int p_month, int p_day, CalendarEvents p_events){
        _year = p_year;
        _month = p_month;
        _day = p_day;
        _events = p_events;

        //_events.setEventsChangeListener(new CalendarEvents.EventsChangeListener() { @Override public void onEventsChange(CalendarEvents events) { refreshEvents(); } });
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
        switch (_month){
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
        } //picks _month string
        textView.setText(String.format("%s %d", mon, _day));
        return v;
    }

    /**
     * Refreshes the ListView.
     */
    private void refreshEvents(){ ((CalendarEventArrayAdapter) super.getListView().getAdapter()).notifyDataSetChanged(); }

    private void goToNextDay(){

    }

    private void goToPreviousDay(){

    }

    private void createEvent(){
        Fragment addEditFragment = new CalendarAddEditFragment(_month, _day, _year, _events);
        getFragmentManager().beginTransaction().replace(R.id.root_view, addEditFragment).addToBackStack(null).commit();
    }

    public void onViewCreated(View view, Bundle bundle){
        super.setListAdapter(new CalendarEventArrayAdapter(view.getContext(), R.layout.calendar_event_layout, _events.getEventsOnDate(_year, _day, _month)));
        refreshEvents();
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