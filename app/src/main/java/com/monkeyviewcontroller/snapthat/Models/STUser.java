package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("_User")
public class STUser extends ParseUser implements Comparable<STUser>{

    public STUser() {

    }

    public String getUsername() {
        return getString("username");
    }

    public String getBirthday() { return getString("birthday"); }

    public int getWins() { return getInt("wins"); }

    public String getEmail() { return getString("email"); }

    public int compareTo(STUser other) {
        return getUsername().compareTo(other.getUsername());
    }
}