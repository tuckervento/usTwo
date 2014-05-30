package com.mill_e.ustwo.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mill_e.ustwo.R;
import com.mill_e.ustwo.UsTwo;
import com.mill_e.ustwo.UsTwoService;

import java.io.IOException;

/**
 * Fragment displaying the CalendarEvent add/edit dialog.
 */
public class CalendarAddEditFragment extends Fragment{

    private class ReminderAdapter extends BaseAdapter implements SpinnerAdapter {
        private final String[] _reminders = new String[]{ "No reminder", "5 min before", "10 min before", "15 min before", "30 min before", "60 min before" };

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int i) {
            return _reminders[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        @Override
        public View getDropDownView(int p_position, View p_convertView, ViewGroup p_parent){
            if (p_convertView == null)
                p_convertView = View.inflate(p_parent.getContext(), android.R.layout.simple_list_item_1, null);
            ((TextView)p_convertView.findViewById(android.R.id.text1)).setText(_reminders[p_position]);
            return p_convertView;
        }
    }

    private String _eventName = "";
    private String _location = "";
    private int _month;
    private int _day;
    private int _year;
    private int _hour = 12;
    private int _minute = 0;
    private int _spinnerPosition;
    private long _timestamp;
    private boolean _editing = false;

    /**
     * Creates a new CalendarAddEditFragment.
     * @param p_month The month of the event
     * @param p_day The day of the event
     * @param p_year The year of the event
     */
    public CalendarAddEditFragment(int p_month, int p_day, int p_year){
        _day = p_day;
        _month = p_month;
        _year = p_year;
    }

    /**
     * Constructs a CalendarAddEditFragment with a pre-determined event name, used for the Edit function.
     * @param p_month The month of the event
     * @param p_day The day of the event
     * @param p_year The year of the event
     * @param p_hour The hour of the event
     * @param p_minute The minute of the event
     * @param p_eventName The name of the event
     * @param p_location The location of the event
     * @param p_reminder The reminder choice for the event
     * @param p_timestamp The timestamp of the event
     */
    public CalendarAddEditFragment(int p_month, int p_day, int p_year, int p_hour, int p_minute, String p_eventName, String p_location, int p_reminder, long p_timestamp){
        _hour = p_hour;
        _minute = p_minute;
        _day = p_day;
        _month = p_month;
        _year = p_year;
        _eventName = p_eventName;
        _location = p_location;
        _spinnerPosition = p_reminder;
        _timestamp = p_timestamp;
        _editing = true;
    }

    private String getFormattedTime(){
        int hour = _hour;
        String ending;

        if (_hour > 12){
            hour = _hour - 12;
            ending = "PM";
        }else if (_hour == 12)
            ending = "PM";
        else
            ending = "AM";

        return String.format("%02d:%02d %s", hour, _minute, ending);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_calendar_add_edit, container, false);
        final Context context = container.getContext();
        final UsTwoService service = ((UsTwo)getActivity()).getService();

        Button remove_button = (Button) v.findViewById(R.id.button_remove_event);
        final Button save_button = (Button) v.findViewById(R.id.button_event_save);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!save_button.isEnabled())
                    save_button.setEnabled(true);
            }
        };

        if (_editing){
            save_button.setEnabled(false);
            remove_button.setEnabled(true);
            remove_button.setVisibility(View.VISIBLE);
        }

        final EditText datePicker = (EditText) v.findViewById(R.id.datePicker_event_date);
        datePicker.setText(String.format("%d/%02d/%d", _month, _day, _year));
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker p_datePicker, int p_year, int p_month, int p_day) {
                        _year = p_year;
                        _month = p_month + 1;
                        _day = p_day;
                        datePicker.setText(String.format("%d/%02d/%d", _month, _day, _year));
                    }
                }, _year, _month - 1, _day);
                datePickerDialog.show();
            }
        });
        datePicker.addTextChangedListener(watcher);

        final EditText timePicker = (EditText) v.findViewById(R.id.timePicker_event_time);
        timePicker.setText(getFormattedTime());
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker p_timePicker, int p_hour, int p_minute) {
                        _hour = p_hour;
                        _minute = p_minute;
                        timePicker.setText(getFormattedTime());
                    }
                }, _hour, _minute, false);
                timePickerDialog.show();
            }
        });
        timePicker.addTextChangedListener(watcher);

        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner_event_reminder);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int p_pos, long l) {
                _spinnerPosition = p_pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setAdapter(new ReminderAdapter());
        if (_editing)
            spinner.setSelection(_spinnerPosition);

        if (_editing)
            save_button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    service.editEvent(_timestamp, _year, _day, _month, _hour, _minute, ((EditText) v.findViewById(R.id.editText_event_name)).getText().toString(),
                            ((EditText) v.findViewById(R.id.editText_event_location)).getText().toString(), _spinnerPosition);
                    getFragmentManager().popBackStack();
                }
            });
        else
            save_button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    service.addEvent(_year, _day, _month, _hour, _minute, ((EditText) v.findViewById(R.id.editText_event_name)).getText().toString(),
                            ((EditText) v.findViewById(R.id.editText_event_location)).getText().toString(), _spinnerPosition);
                    getFragmentManager().popBackStack();
                }
            });

        v.findViewById(R.id.button_back_to_event_listing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        EditText name = (EditText) v.findViewById(R.id.editText_event_name);
        name.setText(_eventName);
        name.addTextChangedListener(watcher);
        EditText location = (EditText) v.findViewById(R.id.editText_event_location);
        location.setText(_location);
        location.addTextChangedListener(watcher);

        return v;
    }
}
