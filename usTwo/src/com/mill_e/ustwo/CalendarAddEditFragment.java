package com.mill_e.ustwo;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Fragment displaying the CalendarEvent add/edit dialog.
 */
public class CalendarAddEditFragment extends Fragment{

    private String _eventName = "";
    private int _month;
    private int _day;
    private int _year;
    private int _hour = 12;
    private int _minute = 0;
    private final UsTwoService _usTwoService;
    private int _spinnerPosition;

    /**
     * Creates a new CalendarAddEditFragment.
     * @param p_month The month of the event
     * @param p_day The day of the event
     * @param p_year The year of the event
     */
    public CalendarAddEditFragment(int p_month, int p_day, int p_year, UsTwoService p_usTwoService){
        _day = p_day;
        _month = p_month;
        _year = p_year;
        _usTwoService = p_usTwoService;
    }

    /**
     * Constructs a CalendarAddEditFragment with a pre-determined event name, used for the Edit function.
     * @param p_month The month of the event
     * @param p_day The day of the event
     * @param p_year The year of the event
     * @param p_eventName The name of the event
     */
    public CalendarAddEditFragment(int p_month, int p_day, int p_year, int p_hour, int p_minute, String p_eventName, UsTwoService p_usTwoService){
        _hour = p_hour;
        _minute = p_minute;
        _day = p_day;
        _month = p_month;
        _year = p_year;
        _eventName = p_eventName;
        _usTwoService = p_usTwoService;
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
        datePicker.setClickable(true);

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
        timePicker.setClickable(true);

        ((Spinner) v.findViewById(R.id.spinner_event_reminder)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int p_pos, long l) { _spinnerPosition = p_pos; }
            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        v.findViewById(R.id.button_event_save).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                _usTwoService.addEvent(_year, _day, _month, _hour, _minute, ((EditText) v.findViewById(R.id.editText_event_name)).getText().toString(),
                        ((EditText) v.findViewById(R.id.editText_event_location)).getText().toString(), _spinnerPosition);
                getFragmentManager().popBackStack();
            }
        });

        ((EditText) v.findViewById(R.id.editText_event_name)).setText(_eventName);

        return v;
    }
}
