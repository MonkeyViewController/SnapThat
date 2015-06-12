package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseUser;

public class CredentialCheckActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ParseUser.getCurrentUser() != null){

            /*try {
                ParseUser.getCurrentUser().fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(new Intent(this, SignupOrLoginActivity.class));
            finish();
        }
    }
}