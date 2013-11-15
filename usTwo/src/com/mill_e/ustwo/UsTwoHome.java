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
import android.provider.Settings;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

/**
 * This is the root activity for UsTwo.
 */
public class UsTwoHome extends Activity implements ActionBar.OnNavigationListener {

	//TODO: Add MQTT support
	//TODO: Add MySQL support
	//TODO: Hugs
	//TODO: Kisses
	//TODO: Lists
	//TODO: Stats
	//TODO: Media
	//TODO: Add notifications to MQTT client (service)
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    /**
     * The username for this app.
     */
    public static String USERNAME;
    /**
     * The MQTT client-id for this app.
     */
    public static String CLIENT_ID;
    /**
     * The active fragment category.
     */
    public static int ACTIVE_FRAGMENT;
    /**
     * The MQTT server URI.
     */
    public static String MQTT_SERVER;
    /**
     * MQTT topic name for Calendar-related messages.
     */
    public static String TOPIC_CALENDAR;
    /**
     * MQTT topic name for List-related messages.
     */
    public static String TOPIC_LISTS;
    /**
     * MQTT topic name for Message-related messages.
     */
    public static String TOPIC_MESSAGES;

    private MessagingFragment _messagingFragment;
    private CalendarFragment _calendarFragment;
    private ListsFragment _listFragment;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _fragmentTransactionId = -1;

            _messagingFragment = new MessagingFragment();
            _calendarFragment = new CalendarFragment();
            _listFragment = new ListsFragment();

            fragmentManager = getFragmentManager();

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
        }
    };

    private int _fragmentTransactionId = -2;
    private int _lastPosition = -1;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_two_home);

        _fragmentTransactionId = -2;

        USERNAME = getString(R.string.user_name);
        CLIENT_ID = Settings.Secure.ANDROID_ID.toString();
        MQTT_SERVER = getString(R.string.mqtt_server);
        TOPIC_CALENDAR = getString(R.string.mqtt_topic_calendar);
        TOPIC_LISTS = getString(R.string.mqtt_topic_lists);
        TOPIC_MESSAGES = getString(R.string.mqtt_topic_messages);

        Intent intent = new Intent(this, UsTwoService.class);
        if (!UsTwoService.STARTED_STATE)
            startService(intent);
        bindService(intent, _serviceConnection, Context.BIND_WAIVE_PRIORITY);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getActionBar().getSelectedNavigationIndex() != ACTIVE_FRAGMENT);
                    //getActionBar().setSelectedNavigationItem(ACTIVE_FRAGMENT);
            }
        });
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            try{getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }catch(IllegalStateException e){ e.printStackTrace(); } //Fixes DE19
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    protected void onPause() {
        try{ unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try{ unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.us_two_home, menu);
        return true;
    }

    /**
     * Tries to hide the keyboard in the given activity.
     * @param activity The activity in which to hide the keyboard
     */
    public static void hideKeyboard(Activity activity){
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch(NullPointerException e){ e.printStackTrace(); }
    }

    private String getFragmentString(int i){
        switch (i){
            case (0):
                return getString(R.string.fragment_messaging_id);
            case (1):
                return getString(R.string.fragment_calendar_id);
            case (2):
                return getString(R.string.fragment_lists_id);
            default:
                return "";
        }
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
                            .replace(R.id.root_view, _messagingFragment, getString(R.string.fragment_messaging_id)).commit();
                else
                    getFragmentManager().popBackStackImmediate(_fragmentTransactionId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    			break;
    		case (1):
    			fragment = _calendarFragment;
                hideKeyboard(this);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    			break;
            case (2):
                fragment = _listFragment;
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
                    .replace(R.id.root_view, fragment, getFragmentString(position))
                    .addToBackStack(getFragmentString(position)).commit();
        }
        _lastPosition = position;
        return true;
    }
}
