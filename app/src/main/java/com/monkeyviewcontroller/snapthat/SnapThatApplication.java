package com.monkeyviewcontroller.snapthat;

import android.app.Application;

import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.parse.Parse;
import com.parse.ParseObject;

public class SnapThatApplication extends Application {

    public void onCreate(){
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,getString(R.string.parse_app_id), getString(R.string.parse_client_id));
        ParseObject.registerSubclass(FriendRequest.class);
    }
}
