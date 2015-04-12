package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText etLoginUsername;
    private EditText etLoginPassword;
    private Button btnLoginLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);

        btnLoginLogin = (Button) findViewById(R.id.btnLoginLogin);
        btnLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    public void attemptLogin()
    {
        Log.d("MVC", "Attempting to Log In");
        etLoginUsername.setError(null);
        etLoginPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        String username = etLoginUsername.getText().toString();
        String password = etLoginPassword.getText().toString();

        if(TextUtils.isEmpty(password))
        {
            etLoginPassword.setError(getString(R.string.error_field_required));
            focusView = etLoginPassword;
            cancel = true;
        }
        else if(!isPasswordValid(password))
        {
            etLoginPassword.setError(getString(R.string.error_invalid_password));
            focusView = etLoginPassword;
            cancel = true;
        }

        if(TextUtils.isEmpty(username))
        {
            etLoginUsername.setError(getString(R.string.error_field_required));
            focusView = etLoginUsername;
            cancel =  true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            Log.d("MVC", "Proceed to login via parse.");
            //show progress bar
            //perform user login
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
