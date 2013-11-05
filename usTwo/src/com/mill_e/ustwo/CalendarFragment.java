package com.mill_e.ustwo;

import android.app.Fragment;
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
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.PopupWindow.OnDismissListener;

/**
 * Fragment for displaying a calendar from which the user can select a date.
 */
public class CalendarFragment extends Fragment implements OnDateChangeListener, OnDismissListener, OnClickListener
{

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsTwoHome.activeFragment = 1;
        View v = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        CalendarView calendarView = (CalendarView)v.findViewById(R.id.root_calendar);
        calendarView.setOnDateChangeListener(this);
        
        return v;
    }
	
	@Override
	public void onSelectedDayChange(CalendarView p_calendarView, int p_year, int p_month, int p_day) {
        Fragment listingFragment = new CalendarEventListingFragment(p_year, p_month+1, p_day);
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
