package com.mill_e.ustwo;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UsTwoHome extends Activity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
	//TODO: Implement Service
	//TODO: Add MQTT support
	//TODO: Add MySQL support
	//TODO: Hugs
	//TODO: Kisses
	//TODO: Lists
	//TODO: Stats
	//TODO: Media
	//TODO: Add notifications to MQTT client (service)
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    public static String userName;
    private MessagingFragment messagingView;
    private CalendarFragment calendarView;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_two_home);
        
        fragmentManager = getFragmentManager();
        messagingView = new MessagingFragment();
        calendarView = new CalendarFragment();
        
        userName = getString(R.string.user_name);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_messaging),
                                getString(R.string.title_calendar),
                                getString(R.string.title_lists),
                                getString(R.string.title_media),
                                getString(R.string.title_settings),
                        }),
                this);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.us_two_home, menu);
        return true;
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
    	Fragment fragment;
    	switch(position){
    		case (0):
				fragment = messagingView;
    			break;
    		case (1):
    			fragment = calendarView;
    			break;
			default:
				fragment = new Fragment(); //just to satisfy uninitialized error
    	}
		getFragmentManager().beginTransaction()
    	.replace(R.id.root_view, fragment)
    	.commit();
        return true;
    }
}
