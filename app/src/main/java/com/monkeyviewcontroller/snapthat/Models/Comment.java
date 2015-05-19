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

    public void setDeleted(boolean b) {
        put("deleted", b);
    }

    public boolean isDeleted() { return getBoolean("deleted"); }

    public void setReports(int count) {
        put("reports", count);
    }

    public int getReports() { return getInt("reports"); }
}
