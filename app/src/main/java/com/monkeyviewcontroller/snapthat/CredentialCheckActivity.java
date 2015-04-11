package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CredentialCheckActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(1==2){//ParseUser.getCurrentUser() != null
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, SignupOrLoginActivity.class));
        }
    }
}
