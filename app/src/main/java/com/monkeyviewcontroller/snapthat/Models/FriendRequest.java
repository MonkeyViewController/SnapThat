package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("FriendRequest")
public class FriendRequest extends ParseObject {

    public FriendRequest() {

    }

    public String getFriendOne() {
        return getString("friendOne");
    }

    public void setFriendOne(String friendOne) {
        put("friendOne", friendOne);
    }

    public String getFriendTwo() {
        return getString("friendTwo");
    }

    public void setFriendTwo(String friendTwo) {
        put("friendTwo", friendTwo);
    }

    public int getStatus() {
        return getInt("status");
    }

    public void setStatus(int status) {
        put("status", status);
    }
}