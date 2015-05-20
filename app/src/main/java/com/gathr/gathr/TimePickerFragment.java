/**************************************************************************************************
 Title : TimePickerFragment.java
 Author : Gathr Team
 Purpose : Picker used when creating gathrings to properly and nicely assess the time
 *************************************************************************************************/

package com.gathr.gathr;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gathr.gathr.classes.MyGlobals;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TextView v;
    MyGlobals global = new MyGlobals();
    public TimePickerFragment(){

    }
    //Constructor to set the proper text view
    public TimePickerFragment(TextView _v){
        v = _v;
    }

    @Override
    //Creates the time picker
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    //Sets the time text field based on the hour and minute provided in the arguments
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView time = v;

        time.setText(global.normalTime(hourOfDay + ":" + minute + ":00"));
    }
}