package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginActivity extends Activity {

    private EditText etLoginUsername;
    private EditText etLoginPassword;
    private Button btnLoginLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
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

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Please wait.");
            pd.setMessage("Logging in, please wait.");
            pd.show();

            ParseUser.logInInBackground(username, password,new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {

                    pd.dismiss();

                    if(e!=null) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(LoginActivity.this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("username",ParseUser.getCurrentUser().getUsername());
                        intent.putExtra("objectId", ParseUser.getCurrentUser().getObjectId());

                        //Associate device-installation with user pointer for Push Notifications
                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("user",ParseUser.getCurrentUser());
                        installation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null){
                                    Log.i("MVC","Issue associating device install with user");
                                    Log.i("MVC", e.getMessage());
                                }
                            }
                        });

                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
