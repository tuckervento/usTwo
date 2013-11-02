package com.mill_e.ustwo;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.PopupWindow.OnDismissListener;

public class CalendarFragment extends Fragment implements OnDateChangeListener, OnDismissListener, OnClickListener
{
	private HashMap<String, CalendarEvent> events;
	
	public CalendarFragment(){
		events = new HashMap<String, CalendarEvent>();
	}
	
	//TODO: Implement adding events
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        View v = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        CalendarView calendarView = (CalendarView)v.findViewById(R.id.root_calendar);
        calendarView.setOnDateChangeListener(this);
        
        return v;
    }
	
	@Override
	public void onSelectedDayChange(CalendarView p_calendarView, int p_year, int p_month, int p_day) {
        Fragment listingFragment = new CalendarEventListingFragment((short) p_year, (short) p_month, (short) p_day);
        getFragmentManager().beginTransaction().replace(R.id.root_view, listingFragment).addToBackStack(null).commit();
	}
	
	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onClick(View v) {
	}
}
