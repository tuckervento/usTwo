package com.mill_e.ustwo;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

public class UsTwoHome extends Activity implements ActionBar.OnNavigationListener {

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

    private Messages _messages;
    private MessagingFragment _messagingView;
    private CalendarEvents _events;
    private CalendarFragment _calendarView;

    private UsTwoService _usTwoService;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _usTwoService = ((UsTwoService.UsTwoBinder)iBinder).getService();
            _usTwoService.setUpDatabases(getApplicationContext());
            _messages = _usTwoService.getMessagesModel();
            _events = _usTwoService.getEventsModel();
            _fragmentTransactionId = -1;

            _messagingView = new MessagingFragment();
            _calendarView = new CalendarFragment();

            fragmentManager = getFragmentManager();

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
                    UsTwoHome.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _usTwoService = null;
        }
    };

    private int _fragmentTransactionId = -2;
    private int _lastPosition = -1;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_two_home);

        Intent intent = new Intent(this, UsTwoService.class);
        startService(intent);
        bindService(intent, _serviceConnection, Context.BIND_WAIVE_PRIORITY);
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

    public static void hideKeyboard(Activity activity){
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch(NullPointerException e){ }
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
    	Fragment fragment = new Fragment();

        if (_fragmentTransactionId == -2)
            return true;

        if (position == _lastPosition)
            return true;

    	switch(position){
    		case (0):
                if (_fragmentTransactionId == -1)
                    getFragmentManager().beginTransaction()
                            .replace(R.id.root_view, _messagingView, getString(R.string.fragment_messaging_id)).commit();
                else
                    getFragmentManager().popBackStackImmediate(_fragmentTransactionId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    			break;
    		case (1):
    			fragment = _calendarView;
                hideKeyboard(this);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    			break;
			default:
                break;
    	}
        if (position != 0){
            if (!getFragmentManager().findFragmentByTag(getString(R.string.fragment_messaging_id)).isVisible())
                getFragmentManager().popBackStackImmediate(_fragmentTransactionId, 0);

            _fragmentTransactionId = getFragmentManager().beginTransaction()
                    .replace(R.id.root_view, fragment, getString(R.string.fragment_calendar_id))
                    .addToBackStack(getString(R.string.fragment_calendar_id)).commit();
        }
        _lastPosition = position;
        return true;
    }
}
