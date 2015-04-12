package com.monkeyviewcontroller.snapthat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText etSignupBirthday;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Log.d("MVC", "Finished onCreateDialog");
        etSignupBirthday = (EditText) getActivity().findViewById(R.id.etSignupBirthday);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Log.d("MVC", "Date has been set");
        etSignupBirthday.setText((month + 1) + "/" + day + "/" + year);
    }
}
