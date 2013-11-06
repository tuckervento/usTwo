package com.mill_e.ustwo;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This is fragment for displaying a list of calendar events.
 */
public class CalendarEventListingFragment extends ListFragment implements OnClickListener{

    private int _year;
    private int _month;
    private int _day;
    private CalendarEvents _events;
    private TextView _headerText;
    private UsTwoService _serviceRef;
    private Context _context;
    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _serviceRef = ((UsTwoService.UsTwoBinder)iBinder).getService();
            _events = _serviceRef.getEventsModel();
            updateEvents(_context);

            _events.setEventsChangeListener(new CalendarEvents.EventsChangeListener() {
                @Override
                public void onEventsChange(CalendarEvents events) {
                    refreshEvents();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _serviceRef = null;
        }
    };


    public CalendarEventListingFragment(int p_year, int p_month, int p_day){
        _year = p_year;
        _month = p_month;
        _day = p_day;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar_event_listing, container, false);
        Button b1 = (Button) v.findViewById(R.id.button_next_day);
        Button b2 = (Button) v.findViewById(R.id.button_previous_day);
        Button b3 = (Button) v.findViewById(R.id.button_create_event);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        _headerText = (TextView) v.findViewById(R.id.textView_listing_day);
        _headerText.setText(getHeaderText());
        _context = container.getContext();
        Intent intent = new Intent(_context, UsTwoService.class);
        _context.bindService(intent, _serviceConnection, Context.BIND_WAIVE_PRIORITY);

        return v;
    }

    private String getHeaderText(){
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
        return String.format("%s %d", mon, _day);
    }

    private void updateEvents(Context p_context){
        super.setListAdapter(new CalendarEventArrayAdapter(p_context, R.layout.calendar_event_layout, _events.getEventsOnDate(_year, _day, _month)));
    }

    /**
     * Refreshes the ListView.
     */
    private void refreshEvents(){
        try{
            ((CalendarEventArrayAdapter) super.getListView().getAdapter()).notifyDataSetChanged();
        }catch(IllegalStateException e){}; //TODO:DE15
    }

    private boolean isThirtyOne(int p_month){
        if (p_month == 1 || p_month == 3 || p_month == 5 || p_month == 7 || p_month == 8 || p_month == 10 || p_month == 12)
            return true;
        return false;
    }

    private boolean isThirty(int p_month){
        if (p_month == 4 || p_month == 6 || p_month == 9 || p_month == 11)
            return true;
        return false;
    }

    private boolean isLeapYear(int p_year){
        if (p_year == 2016 || p_year == 2020 || p_year == 2028)
            return true;
        return false;
    }

    private void goToNextDay(View p_view){
        _day++;

        if ((_day == 32 && isThirtyOne(_month)) || (_day == 31 && isThirty(_month)) ||
                (_month == 2 && ((_day == 29 && !isLeapYear(_year)) || _day == 30))){
            _day = 1;
            _month++;
            if (_month == 13){
                _year++;
                _month = 1;
            }
        }

        _headerText.setText(getHeaderText());
        updateEvents(p_view.getContext());
        refreshEvents();
    }

    private void goToPreviousDay(View p_view){
        _day--;

        if (_day == 0){
            _month--;
            if (_month == 0){
                _year--;
                _day = 31;
                _month = 12;
            }else if (isThirtyOne(_month))
                _day = 31;
            else if (isThirty(_month))
                _day = 30;
            else if (_month == 2 && !isLeapYear(_year))
                _day = 28;
            else
                _day = 29;
        }

        _headerText.setText(getHeaderText());
        updateEvents(p_view.getContext());
        refreshEvents();
    }

    private void createEvent(){
        Fragment addEditFragment = new CalendarAddEditFragment(_month, _day, _year, _serviceRef);
        getFragmentManager().beginTransaction().replace(R.id.root_view, addEditFragment).addToBackStack(null).commit();
    }

    public void onViewCreated(View view, Bundle bundle){
        List<CalendarEvent> events;

        if (_events != null)
            events = _events.getEventsOnDate(_year, _day, _month);
        else
            events = new ArrayList<CalendarEvent>();

        super.setListAdapter(new CalendarEventArrayAdapter(view.getContext(), R.layout.calendar_event_layout, events));
        refreshEvents();
    }

    @Override
    public void onPause() {
        super.onPause();
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ }
    }

    @Override
    public void onClick(View p_view) {
        switch (p_view.getId()){
            case R.id.button_next_day:
                goToNextDay(p_view);
                break;
            case R.id.button_previous_day:
                goToPreviousDay(p_view);
                break;
            case R.id.button_create_event:
                createEvent();
                break;
        }
    }
}