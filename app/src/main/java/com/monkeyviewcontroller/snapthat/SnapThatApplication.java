package com.monkeyviewcontroller.snapthat;

import android.app.Application;
import com.parse.Parse;

public class SnapThatApplication extends Application {

    public void onCreate(){
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,getString(R.string.parse_app_id), getString(R.string.parse_client_id));
    }
}
