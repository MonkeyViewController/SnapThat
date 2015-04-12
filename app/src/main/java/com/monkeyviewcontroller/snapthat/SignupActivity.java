package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends FragmentActivity {

    private Button btnSignupBirthday;
    private Button btnSignupSignup;

    private EditText etSignupUsername;
    private EditText etSignupEmail;
    private EditText etSignupPassword;
    private EditText etSignupBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
        etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
        etSignupPassword = (EditText) findViewById(R.id.etSignupPassword);
        etSignupBirthday = (EditText) findViewById(R.id.etSignupBirthday);

        btnSignupBirthday = (Button)findViewById(R.id.btnSignupBirthday);
        btnSignupBirthday.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked button to select birthday");
                showDatePickerDialog(v);
            }
        });

        btnSignupSignup = (Button) findViewById(R.id.btnSignupSignup);
        btnSignupSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked button to sign up");
                attemptSignup();
            }
        });
    }

    public void attemptSignup()
    {
        etSignupUsername.setError(null);
        etSignupEmail.setError(null);
        etSignupPassword.setError(null);
        etSignupBirthday.setError(null);

        boolean cancel = false;
        View focusView = null;

        String username = etSignupUsername.getText().toString();
        String email = etSignupEmail.getText().toString();
        String birthday = etSignupBirthday.getText().toString();
        String password = etSignupPassword.getText().toString();

        if(TextUtils.isEmpty(birthday))
        {
            etSignupBirthday.setError(getString(R.string.error_field_required));
            focusView = etSignupBirthday;
            cancel = true;
        }

        if(TextUtils.isEmpty(password))
        {
            etSignupPassword.setError(getString(R.string.error_field_required));
            focusView = etSignupPassword;
            cancel = true;
        }

        if(!isPasswordValid(password))
        {
            etSignupPassword.setError(getString(R.string.error_invalid_password));
            focusView = etSignupPassword;
            cancel = true;
        }

        if(TextUtils.isEmpty(email))
        {
            etSignupEmail.setError(getString(R.string.error_field_required));
            focusView = etSignupEmail;
            cancel = true;
        }

        if(TextUtils.isEmpty(username))
        {
            etSignupUsername.setError(getString(R.string.error_field_required));
            focusView = etSignupUsername;
            cancel =  true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            Log.d("MVC", "Proceed to signup via parse.");
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Please wait.");
            pd.setMessage("Signing up, please wait.");
            pd.show();

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.put("birthday",birthday);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    pd.dismiss();

                    if(e!=null) {
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }else {
                        Intent intent = new Intent(SignupActivity.this, SignupOrLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}