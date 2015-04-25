package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("_User")
public class STUser extends ParseUser {

    public STUser() {

    }

    public String getUsername() {
        return getString("username");
    }

    public String getBirthday() { return getString("birthday"); }

    public String getEmail() { return getString("email"); }

    public Model getModel() { return new Model(this); }

    public static class Model implements Serializable {

        private String objectId;
        private String username;

        public Model(STUser user) {
            this.objectId = user.getObjectId();
            this.username = user.getUsername();
        }

        public String getObjectId() {
            return objectId;
        }

        public String getUsername() {
            return username;
        }
    }
}