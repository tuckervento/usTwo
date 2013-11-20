package com.mill_e.ustwo;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * This fragment allows the user to specify settings.
 */
public class UserSettingsFragment extends Fragment {

    private UserSettings _settings;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) { _settings = ((UsTwoService.UsTwoBinder) iBinder).getService().getSettings(); }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsTwoHome.ACTIVE_FRAGMENT = 4;
        final View v = inflater.inflate(R.layout.fragment_settings_view, container, false);

        v.findViewById(R.id.button_save_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _settings.setUserName(((EditText)v.findViewById(R.id.editText_settings_username)).getText().toString());
            }
        });
        ((EditText)v.findViewById(R.id.editText_settings_username)).setText(_settings.getUserName());

        return v;
    }
}
