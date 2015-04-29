package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by isaacsiegel on 4/29/15.
 */
@ParseClassName("Submission")
public class Submission extends ParseObject {

    public Submission(){

    }

    public void setPicture(ParseFile pic){
        put("picture", pic);
    }

    public ParseFile getPicture(){
        return getParseFile("picture");
    }

    public void setCreator(ParseUser value) {
        put("creator", value);
    }

    public String getCreatorUsername() { return getParseUser("creator").getUsername(); }

    public boolean getIsValid(){
        return getBoolean("isValid");
    }

    public void setIsValid(boolean isValid){
        put("isValid", isValid);
    }

    public boolean getHasProcessed(){
        return getBoolean("hasProcessed");
    }

    public void setHasProcessed(boolean hasProcessed){
        put("hasProcessed", hasProcessed);
    }





}
