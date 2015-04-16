package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class STUser extends ParseUser {

    public STUser() {

    }

    public String getUsername() {
        return getString("username");
    }

    public String getBirthday() { return getString("birthday"); }

    public String getEmail() { return getString("email"); }
}