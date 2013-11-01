package com.mill_e.ustwo;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class CalendarFragment extends Fragment implements OnDateChangeListener, OnDismissListener, OnClickListener
{
	private HashMap<String, CalendarEvent> events;
	private PopupWindow window;
	
	public CalendarFragment(){
		events = new HashMap<String, CalendarEvent>();
	}
	
	//TODO: Implement adding events
	//TODO: Fix popup issue
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View popupView = inflater.inflate(R.layout.calendar_event_listing, null);
		Button b = (Button)popupView.findViewById(R.id.close_listing);
		b.setOnClickListener(this);
		window = new PopupWindow(popupView, 100, 100, true);
		window.setOnDismissListener(this);
		
        View v = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        CalendarView calendarView = (CalendarView)v.findViewById(R.id.root_calendar);
        calendarView.setOnDateChangeListener(this);
        
        return v;
    }
	
	@Override
	public void onSelectedDayChange(CalendarView p_calendarView, int p_year, int p_month, int p_day) {
		window.showAtLocation(p_calendarView.getRootView(), Gravity.CENTER, 0, 0);
		window.update();
	}
	
	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onClick(View v) {
		window.dismiss();
	}
}
