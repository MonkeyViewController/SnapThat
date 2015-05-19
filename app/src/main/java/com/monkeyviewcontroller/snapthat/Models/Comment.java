package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public Comment(){

    }

    public Date getCreatedDate() { return getCreatedAt(); }

    public String getMessage() { return getString("message"); }

    public void setMessage(String message) {
        put("message", message);
    }

    public void setCommenter(ParseUser value) {
        put("commenter", value);
    }

    public STUser getCommenter() { return (STUser)getParseUser("commenter"); }

    public void setGame(String game) { put("forGame",  ParseObject.createWithoutData("Game",game)); }
}
