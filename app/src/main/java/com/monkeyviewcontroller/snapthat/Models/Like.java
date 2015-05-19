package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Like")
public class Like extends ParseObject {

    public Like(){

    }

    public Date getCreatedDate() { return getCreatedAt(); }

    public void fromUser(ParseUser value) {
        put("fromUser", value);
    }

    public STUser getLiker() { return (STUser)getParseUser("fromUser"); }

    public void forGame(String game) { put("forGame",  ParseObject.createWithoutData("Game",game)); }
}
