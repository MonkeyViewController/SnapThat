package com.monkeyviewcontroller.snapthat;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SignupActivity extends ActionBarActivity implements View.OnClickListener {

    private Button btnSelectBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSelectBirthday = (Button)findViewById(R.id.btnSelectBirthday);
        btnSelectBirthday.setOnClickListener(this);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSelectBirthday) {
            Log.d("MVC", "Clicked button select birthday");
            showDatePickerDialog(v);
        }
    }
}
