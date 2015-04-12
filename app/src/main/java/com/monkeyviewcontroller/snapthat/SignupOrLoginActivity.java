package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SignupOrLoginActivity extends Activity implements View.OnClickListener {

    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) {
            Log.d("MVC", "Clicked button login");
            Intent intent = new Intent(this, LoginActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (id == R.id.btnSignup) {
            Log.d("MVC", "Clicked button signup");
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
    }
}
