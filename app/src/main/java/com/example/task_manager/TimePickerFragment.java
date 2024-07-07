package com.example.task_manager;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private OnTimeSetListener listener; // Declare listener interface

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(requireActivity(), (view, hourOfDay, minute1) -> {
            if (listener != null) {
                listener.onTimeSet(view, hourOfDay, minute1);
            }
        }, hour, minute, DateFormat.is24HourFormat(requireActivity()));
    }

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.listener = listener;
    }

    // Interface to communicate with the activity
    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}
