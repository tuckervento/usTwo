package com.mill_e.ustwo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CalendarEventListingFragment extends Fragment implements OnClickListener{

    private short year;
    private short month;
    private short day;

    public CalendarEventListingFragment(short p_year, short p_month, short p_day){
        this.year = p_year;
        this.month = p_month;
        this.day = p_day;
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
        TextView textView = (TextView) v.findViewById(R.id.textview_listing_day);
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

    private void goToNextDay(){

    }

    private void goToPreviousDay(){

    }

    private void createEvent(){

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