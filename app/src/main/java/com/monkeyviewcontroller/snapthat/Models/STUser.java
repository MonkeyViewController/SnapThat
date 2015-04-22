package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("_User")
public class STUser extends ParseUser implements Serializable {

    public STUser() {

    }

    public String getUsername() {
        return getString("username");
    }

    public String getBirthday() { return getString("birthday"); }

    public String getEmail() { return getString("email"); }
}