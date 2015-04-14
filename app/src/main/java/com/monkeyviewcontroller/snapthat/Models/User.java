package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    public User() {

    }

    public String getObjectId() {
        return getObjectId();
    }

    public String getUsername() {
        return getString("username");
    }

    public String getBirthday() { return getString("birthday"); }

    public String getEmail() { return getString("email"); }
}