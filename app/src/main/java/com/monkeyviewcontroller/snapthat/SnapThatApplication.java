package com.monkeyviewcontroller.snapthat;

import android.app.Application;

import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.Like;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.Models.Submission;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class SnapThatApplication extends Application {

    public void onCreate(){
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(FriendRequest.class);
        ParseUser.registerSubclass(STUser.class);
        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(Submission.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);

        Parse.initialize(this,getString(R.string.parse_app_id), getString(R.string.parse_client_id));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
