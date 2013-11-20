package com.mill_e.ustwo;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * This fragment allows the user to specify settings.
 */
public class UserSettingsFragment extends Fragment {

    private UserSettings

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) { _serviceRef = ((UsTwoService.UsTwoBinder) iBinder).getService(); }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { _serviceRef = null; }
    };
}
