package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignupOrLoginActivity extends Activity{

    private Button btnLogin;
    private Button btnSignup;
    private Button btnBypass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked button login");
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked button signup");
                Intent intent = new Intent(v.getContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        btnBypass = (Button)findViewById(R.id.btnBypass);
        btnBypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked button bypass");

                ParseUser.logInInBackground("test", "asdfg",new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {

                        if(e!=null) {
                            Toast.makeText(SignupOrLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(SignupOrLoginActivity.this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignupOrLoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}
